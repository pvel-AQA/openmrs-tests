package coverage;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Swagger/OpenAPI-driven API test-coverage tracker for OpenMRS REST Assured tests.
 *
 * Lifecycle:
 *   1. SwaggerCoverage.init("openmrs-swagger-complete.json")  // once, before tests
 *   2. SwaggerCoverage.record(method, concretePath)           // per request (from the filter)
 *   3. SwaggerCoverage.writeReports("target/coverage")        // once, after all tests
 *
 * "Coverage" here = of all METHOD+path operations the spec documents, how many did
 * the test run exercise at least once. Concrete request paths (with real UUIDs) are
 * normalized back to spec templates (/patient/{uuid}) before matching.
 */
public final class SwaggerCoverage {

    /** One documented operation, e.g. GET /patient/{uuid}. */
    public static final class Operation {
        public final String method;        // GET, POST, ...
        public final String path;          // /patient/{uuid}
        public final Pattern regex;        // compiled matcher for concrete paths
        public final int templateVars;     // number of {..} segments (for match specificity)

        Operation(String method, String path) {
            this.method = method;
            this.path = path;
            this.templateVars = (int) path.chars().filter(c -> c == '{').count();
            String rx = "^" + path.replaceAll("\\{[^/}]+}", "[^/]+") + "/?$";
            this.regex = Pattern.compile(rx);
        }
        String key() { return method + " " + path; }
    }

    private static final List<Operation> OPERATIONS = new ArrayList<>();
    private static final Set<String> HIT = ConcurrentHashMap.newKeySet();          // matched op keys
    private static final Set<String> UNMATCHED = ConcurrentHashMap.newKeySet();    // requests w/ no spec op
    private static String basePath = "";
    private static boolean initialized = false;

    private SwaggerCoverage() {}

    // ---- 1. INIT: load the spec -------------------------------------------------
    public static synchronized void init(String specPath) {
        if (initialized) return;
        try {
            JsonNode root = new JsonMapper().readTree(Paths.get(specPath).toFile());
            basePath = root.path("basePath").asText("");
            JsonNode paths = root.path("paths");
            Iterator<String> it = paths.propertyNames().iterator();
            while (it.hasNext()) {
                String p = it.next();
                JsonNode methods = paths.path(p);
                Iterator<String> mIt = methods.propertyNames().iterator();
                while (mIt.hasNext()) {
                    String m = mIt.next();
                    if (isHttpMethod(m)) {
                        OPERATIONS.add(new Operation(m.toUpperCase(Locale.ROOT), p));
                    }
                }
            }
            // longest/most-specific templates first so /patient/{uuid}/name beats /patient/{uuid}
            OPERATIONS.sort(Comparator
                    .comparingInt((Operation o) -> o.path.length()).reversed());
            initialized = true;
            System.out.println("[SwaggerCoverage] loaded " + OPERATIONS.size()
                    + " operations (basePath='" + basePath + "')");
        } catch (Exception e) {
            throw new RuntimeException("Could not load spec: " + specPath, e);
        }
    }

    private static boolean isHttpMethod(String m) {
        switch (m.toLowerCase(Locale.ROOT)) {
            case "get": case "post": case "put": case "delete": case "patch": return true;
            default: return false;
        }
    }

    // ---- 2. RECORD: called per request from the filter --------------------------
    public static void record(String method, String concretePath) {
        if (!initialized) return;
        String norm = normalize(concretePath);
        Operation match = matchOperation(method, norm);
        if (match != null) {
            HIT.add(match.key());
        } else {
            UNMATCHED.add(method.toUpperCase(Locale.ROOT) + " " + norm);
        }
    }

    /** Strip host, basePath, query string, and the OpenMRS /ws/rest/v1 prefix. */
    static String normalize(String raw) {
        String p = raw;
        int q = p.indexOf('?');
        if (q >= 0) p = p.substring(0, q);
        // drop scheme+host if a full URL slipped in
        int schemeIdx = p.indexOf("://");
        if (schemeIdx >= 0) {
            int slash = p.indexOf('/', schemeIdx + 3);
            p = (slash >= 0) ? p.substring(slash) : "/";
        }
        // cut everything up to and including /ws/rest/v1 (or the spec basePath)
        int idx = p.indexOf("/ws/rest/v1");
        if (idx >= 0) {
            p = p.substring(idx + "/ws/rest/v1".length());
        } else if (!basePath.isEmpty() && p.startsWith(basePath)) {
            p = p.substring(basePath.length());
        }
        if (p.isEmpty()) p = "/";
        if (!p.startsWith("/")) p = "/" + p;
        return p;
    }

    private static Operation matchOperation(String method, String normPath) {
        String M = method.toUpperCase(Locale.ROOT);
        Operation best = null;
        for (Operation op : OPERATIONS) {
            if (!op.method.equals(M)) continue;
            if (op.regex.matcher(normPath).matches()) {
                // OPERATIONS is sorted longest-first; first match is most specific
                best = op;
                break;
            }
        }
        return best;
    }

    // ---- 3. REPORT --------------------------------------------------------------
    public static void writeReports(String outDir) {
        if (!initialized) { System.out.println("[SwaggerCoverage] not initialized; no report"); return; }
        try {
            Path dir = Paths.get(outDir);
            Files.createDirectories(dir);

            List<Operation> sorted = OPERATIONS.stream()
                    .sorted(Comparator.comparing((Operation o) -> o.path).thenComparing(o -> o.method))
                    .collect(Collectors.toList());

            int total = sorted.size();
            long covered = sorted.stream().filter(o -> HIT.contains(o.key())).count();
            double pct = total == 0 ? 0 : (100.0 * covered / total);

            writeCsv(dir.resolve("coverage.csv"), sorted);
            writeHtml(dir.resolve("coverage.html"), sorted, total, covered, pct);

            System.out.printf("[SwaggerCoverage] %d/%d operations covered (%.1f%%)%n", covered, total, pct);
            System.out.println("[SwaggerCoverage] reports: " + dir.resolve("coverage.html").toAbsolutePath());
            if (!UNMATCHED.isEmpty()) {
                System.out.println("[SwaggerCoverage] " + UNMATCHED.size()
                        + " request(s) hit paths not in the spec (see report 'Undocumented' section)");
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not write coverage reports", e);
        }
    }

    private static void writeCsv(Path file, List<Operation> ops) throws IOException {
        try (Writer w = Files.newBufferedWriter(file)) {
            w.write("method,path,covered\n");
            for (Operation o : ops) {
                w.write(o.method + "," + o.path + "," + (HIT.contains(o.key()) ? "yes" : "no") + "\n");
            }
        }
    }

    private static void writeHtml(Path file, List<Operation> ops,
                                  int total, long covered, double pct) throws IOException {
        // group by top-level resource for a readable breakdown
        Map<String, List<Operation>> byResource = new TreeMap<>();
        for (Operation o : ops) {
            String res = o.path.replaceFirst("^/", "").split("/")[0];
            byResource.computeIfAbsent(res, k -> new ArrayList<>()).add(o);
        }

        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        StringBuilder rows = new StringBuilder();
        for (Map.Entry<String, List<Operation>> e : byResource.entrySet()) {
            List<Operation> list = e.getValue();
            long c = list.stream().filter(o -> HIT.contains(o.key())).count();
            double rp = 100.0 * c / list.size();
            String barClass = rp == 100 ? "full" : rp == 0 ? "none" : "partial";
            rows.append("<tr class='res'><td class='res-name'>").append(esc(e.getKey()))
                .append("</td><td class='num'>").append(c).append("/").append(list.size())
                .append("</td><td class='barcell'><div class='bar ").append(barClass)
                .append("' style='width:").append(String.format(Locale.ROOT, "%.0f", rp))
                .append("%'></div></td></tr>");
            for (Operation o : list) {
                boolean hit = HIT.contains(o.key());
                rows.append("<tr class='op'><td class='op-method ").append(o.method.toLowerCase(Locale.ROOT))
                    .append("'>").append(o.method).append(" <span class='op-path'>").append(esc(o.path))
                    .append("</span></td><td colspan='2' class='status ").append(hit ? "yes" : "no")
                    .append("'>").append(hit ? "covered" : "not covered").append("</td></tr>");
            }
        }

        StringBuilder undoc = new StringBuilder();
        if (!UNMATCHED.isEmpty()) {
            undoc(undoc);
        }

        String html = HTML_TEMPLATE
                .replace("{{TS}}", ts)
                .replace("{{TOTAL}}", String.valueOf(total))
                .replace("{{COVERED}}", String.valueOf(covered))
                .replace("{{UNCOVERED}}", String.valueOf(total - covered))
                .replace("{{PCT}}", String.format(Locale.ROOT, "%.1f", pct))
                .replace("{{PCTNUM}}", String.format(Locale.ROOT, "%.0f", pct))
                .replace("{{ROWS}}", rows.toString())
                .replace("{{UNDOC}}", undoc.toString());
        Files.write(file, html.getBytes());
    }

    private static void undoc(StringBuilder sb) {
        sb.append("<section class='card'><h2>Undocumented requests <span class='hint'>")
          .append("(hit by tests, not in spec)</span></h2><ul class='undoc'>");
        UNMATCHED.stream().sorted().forEach(u ->
            sb.append("<li>").append(esc(u)).append("</li>"));
        sb.append("</ul></section>");
    }

    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static final String HTML_TEMPLATE =
        "<!DOCTYPE html><html lang='en'><head><meta charset='utf-8'>"
      + "<meta name='viewport' content='width=device-width,initial-scale=1'>"
      + "<title>OpenMRS API Test Coverage</title><style>"
      + ":root{--bg:#0f1420;--card:#1a2030;--ink:#e6ebf5;--muted:#8a96ad;--line:#2a3346;"
      + "--yes:#3ddc97;--no:#ff5d6c;--partial:#ffb454;--get:#61afef;--post:#3ddc97;--delete:#ff5d6c;--put:#c678dd;}"
      + "*{box-sizing:border-box}body{margin:0;background:var(--bg);color:var(--ink);"
      + "font-family:'IBM Plex Sans',-apple-system,Segoe UI,sans-serif;line-height:1.5}"
      + ".wrap{max-width:920px;margin:0 auto;padding:48px 24px}"
      + "h1{font-size:28px;margin:0 0 4px;letter-spacing:-.01em}"
      + ".sub{color:var(--muted);font-size:14px;margin-bottom:32px}"
      + ".summary{display:grid;grid-template-columns:1fr 1fr 1fr auto;gap:16px;margin-bottom:32px}"
      + ".stat{background:var(--card);border:1px solid var(--line);border-radius:12px;padding:20px}"
      + ".stat .n{font-size:32px;font-weight:700;font-variant-numeric:tabular-nums}"
      + ".stat .l{color:var(--muted);font-size:12px;text-transform:uppercase;letter-spacing:.08em;margin-top:4px}"
      + ".ring{display:flex;align-items:center;justify-content:center}"
      + ".ring svg{transform:rotate(-90deg)}.ring .pct{font-size:20px;font-weight:700}"
      + ".card{background:var(--card);border:1px solid var(--line);border-radius:12px;padding:8px 0;margin-bottom:24px;overflow:hidden}"
      + "h2{font-size:15px;margin:16px 20px;color:var(--muted);text-transform:uppercase;letter-spacing:.08em}"
      + "h2 .hint{text-transform:none;letter-spacing:0;font-weight:400;font-size:12px}"
      + "table{width:100%;border-collapse:collapse}"
      + "tr.res{background:#141a28}tr.res td{padding:10px 20px;border-top:1px solid var(--line);font-weight:600}"
      + ".res-name{text-transform:capitalize}.num{font-variant-numeric:tabular-nums;color:var(--muted);width:80px}"
      + ".barcell{width:160px}.bar{height:8px;border-radius:4px;background:var(--partial)}"
      + ".bar.full{background:var(--yes)}.bar.none{background:var(--no);min-width:2px}"
      + "tr.op td{padding:6px 20px;font-size:13px;border-top:1px solid rgba(42,51,70,.4)}"
      + ".op-method{font-family:'IBM Plex Mono',monospace;font-weight:600}"
      + ".op-method.get{color:var(--get)}.op-method.post{color:var(--post)}"
      + ".op-method.delete{color:var(--delete)}.op-method.put{color:var(--put)}"
      + ".op-path{color:var(--ink);font-weight:400}"
      + ".status{text-align:right;font-size:12px;font-weight:600}"
      + ".status.yes{color:var(--yes)}.status.no{color:var(--no)}"
      + ".undoc{margin:0 20px 16px;padding-left:18px;color:var(--partial);font-family:monospace;font-size:13px}"
      + "</style></head><body><div class='wrap'>"
      + "<h1>OpenMRS API Test Coverage</h1>"
      + "<div class='sub'>Generated {{TS}} &middot; based on openmrs-swagger-complete.json</div>"
      + "<div class='summary'>"
      + "<div class='stat'><div class='n'>{{TOTAL}}</div><div class='l'>Operations</div></div>"
      + "<div class='stat'><div class='n' style='color:var(--yes)'>{{COVERED}}</div><div class='l'>Covered</div></div>"
      + "<div class='stat'><div class='n' style='color:var(--no)'>{{UNCOVERED}}</div><div class='l'>Not covered</div></div>"
      + "<div class='stat ring'><div><svg width='72' height='72'>"
      + "<circle cx='36' cy='36' r='30' fill='none' stroke='#2a3346' stroke-width='8'/>"
      + "<circle cx='36' cy='36' r='30' fill='none' stroke='var(--yes)' stroke-width='8' "
      + "stroke-dasharray='188.4' stroke-dashoffset='calc(188.4 - 188.4 * {{PCTNUM}} / 100)' stroke-linecap='round'/>"
      + "</svg><div class='pct' style='margin-top:-46px;text-align:center'>{{PCT}}%</div></div></div>"
      + "</div>"
      + "<section class='card'><h2>Coverage by resource</h2><table>{{ROWS}}</table></section>"
      + "{{UNDOC}}"
      + "</div></body></html>";
}

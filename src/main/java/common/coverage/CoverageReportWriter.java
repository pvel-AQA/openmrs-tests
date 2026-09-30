package common.coverage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class CoverageReportWriter {

    private CoverageReportWriter() {
    }

    public static void writeHtml(Path output,
                                 int registrySize,
                                 int coveredCount,
                                 int notCoveredCount,
                                 double automatedPercent,
                                 Map<String, TestCase> registry,
                                 List<String> notCoveredIds,
                                 List<String> unknownIds) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"en\"><head><meta charset=\"utf-8\">");
        sb.append("<title>Coverage Report</title>");
        sb.append("<style>");
        sb.append("body{font-family:system-ui,-apple-system,sans-serif;max-width:960px;margin:32px auto;padding:0 16px;color:#222}");
        sb.append("h1{font-size:24px}");
        sb.append(".metrics{display:flex;gap:24px;margin:24px 0;flex-wrap:wrap}");
        sb.append(".metric{border:1px solid #ddd;border-radius:8px;padding:12px 20px;min-width:140px}");
        sb.append(".metric .value{font-size:28px;font-weight:600}");
        sb.append(".metric .label{color:#666;font-size:13px;text-transform:uppercase;letter-spacing:.5px}");
        sb.append("table{border-collapse:collapse;width:100%;margin-top:16px}");
        sb.append("th,td{border-bottom:1px solid #eee;padding:8px 12px;text-align:left}");
        sb.append("th{background:#fafafa;font-weight:600}");
        sb.append(".tag{display:inline-block;background:#eef;color:#336;border-radius:4px;padding:2px 8px;font-family:monospace;font-size:13px}");
        sb.append(".ok{color:#2a7}");
        sb.append(".warn{color:#c33}");
        sb.append("</style></head><body>");

        sb.append("<h1>Coverage Report</h1>");
        sb.append("<p>Generated: ").append(Instant.now()).append("</p>");

        sb.append("<div class=\"metrics\">");
        sb.append(metric(String.format("%.2f%%", automatedPercent), "Automated %"));
        sb.append(metric(String.valueOf(registrySize), "Registry size"));
        sb.append(metric(String.valueOf(coveredCount), "Covered"));
        sb.append(metric(String.valueOf(notCoveredCount), "Not covered"));
        sb.append(metric(String.valueOf(unknownIds.size()), "Unknown in Allure"));
        sb.append("</div>");

        if (notCoveredIds.isEmpty()) {
            sb.append("<p class=\"ok\">All registry cases are covered by automated tests.</p>");
        } else {
            sb.append("<h2>Not covered (in registry, no automated test)</h2>");
            sb.append("<table><thead><tr><th>Case ID</th><th>Title</th></tr></thead><tbody>");
            for (String id : notCoveredIds) {
                TestCase tc = registry.get(id);
                sb.append("<tr>");
                sb.append("<td><span class=\"tag\">").append(escape(id)).append("</span></td>");
                sb.append("<td>").append(escape(tc != null ? tc.title() : "")).append("</td>");
                sb.append("</tr>");
            }
            sb.append("</tbody></table>");
        }

        if (!unknownIds.isEmpty()) {
            sb.append("<h2 class=\"warn\">Unknown in Allure (tag present, case not in registry)</h2>");
            sb.append("<table><thead><tr><th>Case ID</th></tr></thead><tbody>");
            for (String id : unknownIds) {
                sb.append("<tr><td><span class=\"tag\">").append(escape(id)).append("</span></td></tr>");
            }
            sb.append("</tbody></table>");
        }

        sb.append("</body></html>");

        try {
            Path parent = output.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(output, sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String metric(String value, String label) {
        return "<div class=\"metric\"><div class=\"value\">" + escape(value)
                + "</div><div class=\"label\">" + escape(label) + "</div></div>";
    }

    private static String escape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}

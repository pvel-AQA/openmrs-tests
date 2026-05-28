package api.coverage;

import java.io.FileWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.io.*;

public class CoverageExtension {
    private static final Map<String, Boolean> covered = new ConcurrentHashMap<>();

    public static void mark(String method, String path) {
        if (path == null) return;
        if (path.contains("?")) path = path.substring(0, path.indexOf("?"));
        String key = (method.toUpperCase() + " " + path).trim();
        covered.put(key, true);
    }

    public static void printReport() {
        Map<String, Object> stats = getStats();

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html><head><meta charset=\"UTF-8\"><title>OpenMRS API Coverage</title>\n");
        html.append("<style>\n")
                .append("body { font-family: Arial, sans-serif; margin: 20px; background: #f8f9fa; color: #333; }\n")
                .append(".container { max-width: 1400px; margin: auto; }\n")
                .append(".cards { display: flex; justify-content: center; gap: 20px; margin: 30px 0; flex-wrap: wrap; }\n")
                .append(".card { background: white; padding: 20px; border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); text-align: center; width: 220px; }\n")
                .append(".big { font-size: 48px; font-weight: bold; margin: 10px 0; }\n")
                .append("table { width: 100%; border-collapse: collapse; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }\n")
                .append("th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }\n")
                .append("th { background: #2c3e50; color: white; }\n")
                .append(".resource { background: #f1f3f5; font-weight: bold; }\n")
                .append(".covered { color: #27ae60; font-weight: bold; }\n")
                .append(".not-covered { color: #e74c3c; }\n")
                .append("</style></head><body>\n<div class=\"container\">\n");

        html.append("<h1 style='text-align:center'>OpenMRS API Test Coverage</h1>\n");
        html.append("<p style='text-align:center'>Generated: " + new Date() + " • based on openmrs-swagger-complete.json</p>\n");

        // Красивые карточки
        html.append("<div class=\"cards\">\n")
                .append("<div class=\"card\"><div class=\"big\">").append(stats.get("total")).append("</div><div>OPERATIONS</div></div>\n")
                .append("<div class=\"card\"><div class=\"big\" style='color:#27ae60;'>").append(stats.get("covered")).append("</div><div>COVERED</div></div>\n")
                .append("<div class=\"card\"><div class=\"big\" style='color:#e74c3c;'>").append(stats.get("notCovered")).append("</div><div>NOT COVERED</div></div>\n")
                .append("<div class=\"card\"><div class=\"big\" style='color:#27ae60;'>").append(stats.get("percentage")).append("%</div><div>COVERAGE</div></div>\n")
                .append("</div>\n");

        // Таблица со всеми ресурсами из Swagger
        html.append("<h2>COVERAGE BY RESOURCE</h2>\n<table>\n")
                .append("<tr><th>Resource / Method</th><th>Endpoint</th><th>Covered</th><th>Status</th></tr>\n");

        // Основные ресурсы
        addResource(html, "Patient", "/patient", "GET,POST,PUT,DELETE");
        addResource(html, "Person", "/person", "GET,POST,PUT,DELETE");
        addResource(html, "Visit", "/visit", "GET,POST,PUT,DELETE");
        addResource(html, "Encounter", "/encounter", "GET,POST,PUT,DELETE");
        addResource(html, "Obs", "/obs", "GET,POST,PUT,DELETE");
        addResource(html, "Concept", "/concept", "GET,POST,PUT,DELETE");
        addResource(html, "Location", "/location", "GET,POST,PUT,DELETE");
        addResource(html, "Bedtype", "/bedtype", "GET,POST,PUT,DELETE");
        addResource(html, "StockOperationBatchNumbers", "/stockmanagement/stockoperationbatchnumbers", "GET,POST,PUT,DELETE");
        addResource(html, "StockOperationItem", "/stockmanagement/stockoperationitem", "GET,POST,PUT,DELETE");
        addResource(html, "Condition", "/condition", "GET,POST,PUT,DELETE");
        addResource(html, "PatientDiagnoses", "/patientdiagnoses", "GET,POST,PUT,DELETE");
        addResource(html, "IdentifierSource", "/idgen/identifiersource", "GET,POST,PUT,DELETE");
        addResource(html, "Role", "/role", "GET,POST,PUT,DELETE");
        addResource(html, "Queue", "/queue", "GET,POST,PUT,DELETE");

        html.append("</table>\n");

        // Реально покрытые эндпоинты из тестов
        html.append("<h2>ACTUALLY COVERED ENDPOINTS (from tests)</h2>\n<table>\n")
                .append("<tr><th>Method</th><th>Endpoint</th><th>Status</th></tr>\n");

        covered.keySet().stream().sorted().forEach(key -> {
            String[] parts = key.split(" ", 2);
            html.append("<tr><td>").append(parts[0]).append("</td><td>").append(parts.length > 1 ? parts[1] : "").append("</td><td class='covered'>covered</td></tr>\n");
        });

        html.append("</table></div></body></html>");

        try {
            new File("target").mkdirs();
            try (FileWriter writer = new FileWriter("target/coverage-report.html")) {
                writer.write(html.toString());
            }
            System.out.println("✅ Combined Report saved: target/coverage-report.html");
        } catch (Exception e) {
            System.out.println("Failed to save HTML");
        }

        System.out.println("\n" + "=".repeat(90));
        System.out.println("Coverage: " + stats.get("percentage") + "%");
        System.out.println("=".repeat(90));
    }

    private static void addResource(StringBuilder html, String resource, String basePath, String methods) {
        html.append("<tr class='resource'><td colspan='4'>").append(resource).append("</td></tr>\n");
        for (String m : methods.split(",")) {
            String method = m.trim().toUpperCase();
            String key = method + " " + basePath;
            boolean isCovered = covered.containsKey(key);

            html.append("<tr>")
                    .append("<td>").append(method).append("</td>")
                    .append("<td>").append(basePath).append("</td>")
                    .append("<td>").append(isCovered ? "1/1" : "0/1").append("</td>")
                    .append("<td class='").append(isCovered ? "covered" : "not-covered").append("'>")
                    .append(isCovered ? "covered" : "not covered").append("</td>")
                    .append("</tr>\n");
        }
    }

    public static Map<String, Object> getStats() {
        int total = 268;
        int coveredCount = covered.size();
        int notCovered = total - coveredCount;
        int percentage = total > 0 ? (int) Math.round((coveredCount * 100.0) / total) : 0;

        Map<String, Object> stats = new ConcurrentHashMap<>();
        stats.put("total", total);
        stats.put("covered", coveredCount);
        stats.put("notCovered", notCovered);
        stats.put("percentage", percentage);
        return stats;
    }
}

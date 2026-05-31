package TomasCepukas_PI24SN_IS_5Uzduotis.service;

import TomasCepukas_PI24SN_IS_5Uzduotis.model.PasswordEntry;

import java.util.ArrayList;
import java.util.List;

public class CsvService {

    public String toCsv(List<PasswordEntry> entries) {
        StringBuilder builder = new StringBuilder();

        builder.append("Pavadinimas;UzsifruotasSlaptazodis;URL;Pastabos")
                .append(System.lineSeparator());

        for (PasswordEntry entry : entries) {
            builder.append(escape(entry.getTitle())).append(";")
                    .append(escape(entry.getEncryptedPassword())).append(";")
                    .append(escape(entry.getUrl())).append(";")
                    .append(escape(entry.getNotes()))
                    .append(System.lineSeparator());
        }

        return builder.toString();
    }

    public List<PasswordEntry> fromCsv(String csvText) {
        List<PasswordEntry> entries = new ArrayList<>();

        if (csvText == null || csvText.isBlank()) {
            return entries;
        }

        String[] lines = csvText.split("\\R");

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];

            if (line.isBlank()) {
                continue;
            }

            List<String> parts = splitLine(line);

            if (parts.size() == 4) {
                PasswordEntry entry = new PasswordEntry(
                        unescape(parts.get(0)),
                        unescape(parts.get(1)),
                        unescape(parts.get(2)),
                        unescape(parts.get(3))
                );

                entries.add(entry);
            }
        }

        return entries;
    }

    private String escape(String text) {
        if (text == null) {
            return "";
        }

        return text
                .replace("\\", "\\\\")
                .replace(";", "\\s")
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    private String unescape(String text) {
        if (text == null) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        boolean escaping = false;

        for (char c : text.toCharArray()) {
            if (escaping) {
                if (c == 's') {
                    result.append(';');
                } else if (c == 'n') {
                    result.append('\n');
                } else {
                    result.append(c);
                }

                escaping = false;
            } else if (c == '\\') {
                escaping = true;
            } else {
                result.append(c);
            }
        }

        if (escaping) {
            result.append('\\');
        }

        return result.toString();
    }

    private List<String> splitLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        boolean escaping = false;

        for (char c : line.toCharArray()) {
            if (escaping) {
                current.append('\\').append(c);
                escaping = false;
            } else if (c == '\\') {
                escaping = true;
            } else if (c == ';') {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        if (escaping) {
            current.append('\\');
        }

        result.add(current.toString());

        return result;
    }
}
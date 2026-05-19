package com.sgu.admissor.util;

import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public final class ExcelFileClassifier {
    private static final Pattern NON_ALNUM = Pattern.compile("[^a-z0-9 ]");

    private static final List<String> HEADER_MARKERS = Arrays.asList(
        "cccd", "cmnd", "ma ctdt", "ma nganh", "ma xet tuyen", "ten nganh",
        "chi tieu", "nguong dau vao", "diem san", "to hop", "ma to hop",
        "dotthi", "dot thi", "mamonthi", "ma mon thi", "thangdiem", "chung chi",
        "diem quy doi", "diem cong", "loai giai", "thu tu nv", "ma truong",
        "ma ctdt", "ten ctdt", "ten nganh chuong trinh", "ten ma xet tuyen",
        "ma mon", "ten mon", "ngay sinh", "madotthi", "ngaythi", "namthi",
        "tendvtctdl", "madvtctdl", "diem bac chung chi", "chung chi ngoai ngu"
    );

    public enum FileType {
        CHI_TIEU,
        NGUONG_DAU_VAO,
        TO_HOP_MON,
        DS_THI_SINH,
        DIEM_DGNL_VSAT,
        NGUYEN_VONG,
        UU_TIEN_XET_TUYEN,
        QUY_DOI_TIENG_ANH,
        UNKNOWN
    }

    public static final class ClassificationResult {
        private final File file;
        private final FileType type;
        private final int score;
        private final String reason;

        public ClassificationResult(File file, FileType type, int score, String reason) {
            this.file = file;
            this.type = type;
            this.score = score;
            this.reason = reason;
        }

        public File getFile() {
            return file;
        }

        public FileType getType() {
            return type;
        }

        public int getScore() {
            return score;
        }

        public String getReason() {
            return reason;
        }
    }

    public static List<ClassificationResult> classifyAll(File[] files) {
        List<ClassificationResult> results = new ArrayList<>();
        if (files == null) {
            return results;
        }
        for (File file : files) {
            if (file == null) {
                continue;
            }
            results.add(classify(file));
        }
        return results;
    }

    public static ClassificationResult classify(File file) {
        HeaderInfo info = readHeaderInfo(file);
        if (info.headerTokens.isEmpty()) {
            return new ClassificationResult(file, FileType.UNKNOWN, 0, "Không đọc được header");
        }
        FileType type = detectFileType(info);
        if (type == FileType.UNKNOWN) {
            String reason = buildReason(info);
            System.err.println("[Classifier] Cannot classify file: " + safeFileName(file));
            System.err.println("[Classifier] " + reason);
            return new ClassificationResult(file, FileType.UNKNOWN, 0, reason);
        }
        return new ClassificationResult(file, type, 1, "Matched by rules");
    }

    private static FileType detectFileType(HeaderInfo info) {
        if (isChiTieu(info)) return FileType.CHI_TIEU;
        if (isNguongDauVao(info)) return FileType.NGUONG_DAU_VAO;
        if (isToHopMon(info)) return FileType.TO_HOP_MON;
        if (isThiSinh(info)) return FileType.DS_THI_SINH;
        if (isDgnlVsat(info)) return FileType.DIEM_DGNL_VSAT;
        if (isNguyenVong(info)) return FileType.NGUYEN_VONG;
        if (isUuTien(info)) return FileType.UU_TIEN_XET_TUYEN;
        if (isQuyDoi(info)) return FileType.QUY_DOI_TIENG_ANH;
        return FileType.UNKNOWN;
    }

    private static boolean isChiTieu(HeaderInfo info) {
        return hasHeader(info, "chi tieu")
            && (hasHeader(info, "ma ctdt") || hasHeader(info, "ma nganh"))
            && (hasHeader(info, "ten ctdt") || hasHeader(info, "ten nganh"));
    }

    private static boolean isNguongDauVao(HeaderInfo info) {
        return (hasHeader(info, "nguong dau vao") || hasHeader(info, "diem san"))
            && (hasHeader(info, "ma xet tuyen") || hasHeader(info, "ma nganh"))
            && (hasHeader(info, "ten nganh") || hasHeader(info, "ten nganh chuong trinh"));
    }

    private static boolean isToHopMon(HeaderInfo info) {
        return (hasHeader(info, "ma to hop") || hasHeader(info, "to hop"))
            && (hasHeader(info, "manganh") || hasHeader(info, "ma nganh"))
            && (hasHeader(info, "do lech") || hasHeader(info, "goc") || hasHeader(info, "tb_keys") || info.hasToHopPattern)
            && (hasHeader(info, "ten to hop") || info.hasToHopPattern);
    }

    private static boolean isThiSinh(HeaderInfo info) {
        return hasHeader(info, "cccd")
            && hasHeader(info, "ho ten")
            && hasHeader(info, "ngay sinh")
            && (hasHeader(info, "gioi tinh") || hasHeader(info, "khuvuc") || hasHeader(info, "khu vu"))
            && countSubjectHeaders(info) >= 3;
    }

    private static boolean isDgnlVsat(HeaderInfo info) {
        return (hasHeader(info, "dotthi") || hasHeader(info, "dot thi") || hasHeader(info, "madotthi"))
            && (hasHeader(info, "mamonthi") || hasHeader(info, "ma mon thi") || hasHeader(info, "ma mon"))
            && hasHeader(info, "thangdiem")
            && (hasHeader(info, "cmnd") || hasHeader(info, "cccd"));
    }

    private static boolean isNguyenVong(HeaderInfo info) {
        return hasHeader(info, "thu tu nv")
            && hasHeader(info, "ma truong")
            && (hasHeader(info, "ma xet tuyen") || hasHeader(info, "ma nganh"))
            && (hasHeader(info, "ten truong") || hasHeader(info, "ten ma xet tuyen"));
    }

    private static boolean isUuTien(HeaderInfo info) {
        return hasHeader(info, "cccd")
            && hasHeader(info, "cap")
            && hasHeader(info, "loai giai")
            && hasHeader(info, "diem cong")
            && hasHeader(info, "ma mon");
    }

    private static boolean isQuyDoi(HeaderInfo info) {
        return (hasHeader(info, "chung chi ngoai ngu") || hasHeader(info, "chung chi"))
            && hasHeader(info, "diem quy doi")
            && hasHeader(info, "diem cong")
            && hasHeader(info, "cccd");
    }

    private static boolean hasHeader(HeaderInfo info, String phrase) {
        String normalized = normalize(phrase);
        for (String header : info.headerTokens) {
            if (header.contains(normalized)) {
                return true;
            }
        }
        return false;
    }

    private static int countSubjectHeaders(HeaderInfo info) {
        String[] subjects = {"to", "va", "li", "ho", "si", "su", "di", "gdcd", "nn", "ktpl", "ti", "cncn", "cnnn"};
        int count = 0;
        for (String subject : subjects) {
            if (hasHeader(info, subject)) {
                count++;
            }
        }
        return count;
    }

    private static HeaderInfo readHeaderInfo(File file) {
        HeaderInfo info = new HeaderInfo();
        if (file == null) {
            return info;
        }
        DataFormatter formatter = new DataFormatter(Locale.getDefault());
        Exception streamingError = null;
        try (InputStream is = new FileInputStream(file);
             Workbook wb = StreamingReader.builder().rowCacheSize(50).bufferSize(4096).open(is)) {
            readHeaderTokensFromWorkbook(info, wb, formatter, 10);
        } catch (Exception e) {
            streamingError = e;
        }

        Exception xssfError = null;
        if (info.headerTokens.isEmpty()) {
            try (InputStream is = new FileInputStream(file);
                 Workbook wb = new XSSFWorkbook(is)) {
                readHeaderTokensFromWorkbook(info, wb, formatter, 10);
            } catch (Exception e) {
                xssfError = e;
            }
        }

        if (info.headerTokens.isEmpty()) {
            String streamingMsg = streamingError != null ? streamingError.getMessage() : "";
            String xssfMsg = xssfError != null ? xssfError.getMessage() : "";
            System.err.println("[Classifier] Cannot read header: " + safeFileName(file));
            if (streamingError != null) {
                System.err.println("[Classifier] StreamingReader failed: " + streamingMsg);
            }
            if (xssfError != null) {
                System.err.println("[Classifier] XSSFWorkbook failed: " + xssfMsg);
            }
        }
        return info;
    }

    private static void readHeaderTokensFromWorkbook(HeaderInfo info, Workbook wb, DataFormatter formatter, int rowLimit) {
        int sheetCount = Math.min(wb.getNumberOfSheets(), 3);
        for (int s = 0; s < sheetCount; s++) {
            Sheet sheet = wb.getSheetAt(s);
            if (sheet == null) {
                continue;
            }
            List<RowData> rowDataList = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() >= rowLimit) {
                    break;
                }
                List<String> cells = new ArrayList<>();
                int nonEmpty = 0;
                short lastCell = row.getLastCellNum();
                if (lastCell <= 0) {
                    continue;
                }
                for (int i = 0; i < lastCell; i++) {
                    Cell cell = row.getCell(i);
                    String raw = formatter.formatCellValue(cell);
                    if (raw == null || raw.trim().isEmpty()) {
                        continue;
                    }
                    String normalized = normalize(raw);
                    if (!normalized.isEmpty()) {
                        cells.add(normalized);
                        nonEmpty++;
                        if (raw.contains("(") && raw.contains("-") && raw.contains(")")) {
                            info.hasToHopPattern = true;
                        }
                    }
                }
                if (nonEmpty > 0) {
                    rowDataList.add(new RowData(row.getRowNum(), cells));
                }
            }

            for (RowData rowData : rowDataList) {
                info.headerTokens.addAll(rowData.cells);
            }
        }
    }

    private static String buildReason(HeaderInfo info) {
        if (info.headerTokens.isEmpty()) {
            return "Không đọc được header";
        }
        List<String> sample = new ArrayList<>();
        int count = 0;
        for (String token : info.headerTokens) {
            sample.add(token);
            count++;
            if (count >= 12) {
                break;
            }
        }
        return "Không đủ tín hiệu phân loại. Header mẫu: " + String.join(", ", sample);
    }

    private static String safeFileName(File file) {
        if (file == null) {
            return "(null)";
        }
        String name = file.getName();
        return name != null ? name : file.getPath();
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
            .replace("đ", "d")
            .replace("Đ", "D");
        normalized = normalized.replaceAll("\\p{M}", "");
        normalized = normalized.toLowerCase(Locale.getDefault());
        normalized = NON_ALNUM.matcher(normalized).replaceAll(" ");
        normalized = normalized.replaceAll("\\s+", " ").trim();
        return normalized;
    }

    private static final class HeaderInfo {
        private final Set<String> headerTokens = new HashSet<>();
        private boolean hasToHopPattern;
    }

    private static final class RowData {
        private final int rowIndex;
        private final List<String> cells;

        private RowData(int rowIndex, List<String> cells) {
            this.rowIndex = rowIndex;
            this.cells = cells;
        }
    }
}

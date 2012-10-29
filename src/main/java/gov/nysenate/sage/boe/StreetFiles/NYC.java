package gov.nysenate.sage.boe.StreetFiles;

import gov.nysenate.sage.boe.AddressUtils;
import gov.nysenate.sage.boe.BOEAddressRange;
import gov.nysenate.sage.boe.StreetFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.dbutils.QueryRunner;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class NYC extends StreetFile {
    private final String town;

    public NYC(int countyCode, String town, File street_file) throws Exception {
        super(countyCode, street_file);
        this.town = town;
    }

    private static class Column {
        public int width;
        public ArrayList<String> lines;

        public Column(int width) {
            this.width = width;
            this.lines = new ArrayList<String>();
        }
    }

    private static class Page {
        private static final Pattern headerPattern = Pattern.compile("(_{3,}\\s*)");
        private static final Pattern footerPattern = Pattern.compile("STREET FINDER LIST");
        public Column[] columns;

        public Page(int column_count) {
            columns = new Column[column_count];
        }

        public static boolean isHeader(String line) {
            return headerPattern.matcher(line).find();
        }

        public static boolean isFooter(String line) {
            return footerPattern.matcher(line).find();
        }

        public void useHeader(String line) {
            // Get the lengths for each column from the header using an
            // "each column ends when the next one starts approach."
            // This doesn't work for the last column because trailing
            // whitespace has been trimmed. We account for this later on.
            Matcher headerMatcher = headerPattern.matcher(line);
            for (int i=0; i<columns.length; i++) {
                // This should always succeed, so just die if it doesn't
                if (headerMatcher.find()) {
                    String columnHeader = headerMatcher.group(1);
                    columns[i] = new Column(columnHeader.length());
                } else {
                    System.out.println("Failed match column "+i+" on: "+line);
                    System.exit(1);
                }
            }
        }

        public void addLine(String line) {
            int start_index = 0, end_index = 0;
            for (int i=0; i<columns.length; i++) {
                Column column = columns[i];

                // In general, the column starts where the last one stopped.
                // The last column can overshoot the header the trailing white
                // space has been trimmed from the document. See headerPattern.
                // Some rows on a page be missing the last columns and we need
                // to be careful to stay in bounds. Again, because trailing
                // white space has been removed in the input text document.
                if(line.length()==0) {
                    end_index = 0;
                } else {
                    end_index = start_index+column.width;
                    if (i+1 == columns.length || end_index > line.length()) {
                        end_index = line.length();
                    }
                }

                column.lines.add(line.substring(start_index, end_index));
                start_index = end_index;
            }
        }

        public ArrayList<String> toSingleColumn() {
            // Flatten each page into 1 column putting lines from each column
            // under the lines from the previous column
            ArrayList<String> lines = new ArrayList<String>();
            for (Column column : columns) {
                lines.addAll(column.lines);
            }
            return lines;
        }

    }

    public ArrayList<String> convertToSingleColumn(BufferedReader br) throws IOException {
        final int COLUMN_COUNT = 3;

        // Use the Page class to parse through all the lines of the file.
        String line;
        ArrayList<Page> pages = new ArrayList<Page>();
        while( (line = br.readLine()) != null) {
            // Skip all lines up to the header
            if (!Page.isHeader(line)) continue;

            // The header marks the beginning of the page
            Page page = new Page(COLUMN_COUNT);
            page.useHeader(line);

            while((line = br.readLine()) != null) {
                if (Page.isFooter(line)) break;

                page.addLine(line);
            }
            pages.add(page);
        }

        // Flatten the file down to a single column
        ArrayList<String> lines = new ArrayList<String>();
        for (Page page : pages) {
            lines.addAll(page.toSingleColumn());
        }

        return lines;
    }

    @Override
    public void save(MysqlDataSource db) throws Exception {
        QueryRunner query_runner = new QueryRunner(db);
        BufferedReader br = new BufferedReader(new FileReader(street_file));
        String aptRegex = "([0-9]+)(?:[-]([0-9A-Z]+))?";
        Pattern rangePattern = Pattern.compile("(?:\\s*"+aptRegex+"\\s+"+aptRegex+")?\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)");

//        Matcher test = rangePattern.matcher("     1       33    45   31   11422    5   10   4   31");
//        test.find();
//        for (int i=0; i<=test.groupCount(); i++) {
//            System.out.println(test.group(i));
//        }
//        System.exit(1);


        BOEAddressRange addressRange = null;
        for (String line : convertToSingleColumn(br)) {
            if (line.trim().length() == 0) continue;

            System.out.println(line);
            if(addressRange == null) {
                // Address Street Line
                addressRange = new BOEAddressRange();
                addressRange.street = line.trim().replaceAll("\\s+"," ");
                addressRange.town = this.town;
                addressRange.state = "NY";
                addressRange.countyCode = county_code;
                AddressUtils.normalizeAddress(addressRange);
                System.out.println(addressRange.street);

            } else {
                Matcher rangeMatcher = rangePattern.matcher(line);
                if (rangeMatcher.find()) {
                    addressRange.aptLoNum = (rangeMatcher.group(1) != null ? Integer.parseInt(rangeMatcher.group(1)) : 0);
                    addressRange.aptLoChr = rangeMatcher.group(2);
                    addressRange.aptHiNum = (rangeMatcher.group(3) != null ? Integer.parseInt(rangeMatcher.group(3)) : 0);
                    addressRange.aptHiChr = rangeMatcher.group(4);
                    addressRange.electionCode = (rangeMatcher.group(5) != null ? Integer.parseInt(rangeMatcher.group(5)) : 0);
                    addressRange.assemblyCode = (rangeMatcher.group(6) != null ? Integer.parseInt(rangeMatcher.group(6)) : 0);
                    addressRange.zip5 = (rangeMatcher.group(7) != null ? Integer.parseInt(rangeMatcher.group(7)) : 0);
                    addressRange.congressionalCode = (rangeMatcher.group(8) != null ? Integer.parseInt(rangeMatcher.group(8)) : 0);
                    addressRange.senateCode = (rangeMatcher.group(9) != null ? Integer.parseInt(rangeMatcher.group(9)) : 0);
                    // addressRange.mc?? = (rangeMatcher.group(10) != null ? Integer.parseInt(rangeMatcher.group(10)) : 0);
                    // addressRange.co?? = (rangeMatcher.group(11) != null ? Integer.parseInt(rangeMatcher.group(11)) : 0);
                    save_record(addressRange, query_runner);
                } else {
                    // Address Street Line
                    addressRange = new BOEAddressRange();
                    addressRange.street = line.trim().replaceAll("\\s+"," ");
                    addressRange.town = this.town;
                    addressRange.state = "NY";
                    addressRange.countyCode = county_code;
                    AddressUtils.normalizeAddress(addressRange);
                    System.out.println(addressRange.street);
                }
            }
        }
    }
    /*
    public static void main(String[] args) throws Exception {
        Ini config = new Ini(new File("config.ini"));

        MysqlDataSource db = new MysqlDataSource();
        Section dbconfig = config.get("database");
        db.setUser(dbconfig.get("user"));
        db.setPassword(dbconfig.get("pass"));
        db.setServerName(dbconfig.get("host"));
        db.setDatabaseName(dbconfig.get("name"));

        NYC queens = new NYC(63,"Queens",new File("/home/graylin/projects/boe2db/2012/Queens_County_Street_Finder.txt"));
        queens.clear(db);
        queens.save(db);
    }
    */
}

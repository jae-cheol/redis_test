import rdbparser.RdbParser;
import rdbparser.entry.Entry;
import rdbparser.entry.Eof;
import rdbparser.entry.KeyValuePair;
import rdbparser.entry.SelectDb;

import java.io.File;

public class RdbFilePrinter {

    public static void printRdbFile(File file) throws Exception {
        System.out.println("2024 연구과제 레디스 dump.rdb 스냅샷 Binary 파일 데이터 조회 프로그램 시작");
        try (RdbParser parser = new RdbParser(file)) {
            Entry e;
            while ((e = parser.readNext()) != null) {
                switch (e.getType()) {

                    case SELECT_DB:
                        System.out.println("Processing DB: " + ((SelectDb)e).getId());
                        System.out.println("------------");
                        break;

                    case EOF:
                        System.out.print("End of file. Checksum: ");
                        for (byte b : ((Eof)e).getChecksum()) {
                            System.out.print(String.format("%02x", b & 0xff));
                        }
                        System.out.println();
                        System.out.println("------------");
                        break;

                    case KEY_VALUE_PAIR:
                        System.out.println("Key value pair");
                        KeyValuePair kvp = (KeyValuePair)e;
                        System.out.println("Key: " + new String(kvp.getKey(), "ASCII"));
//                        Long expireTime = kvp.getExpiretime();
//                        if (expireTime != null) {
//                            System.out.println("Expire time (ms): " + expireTime);
//                        }
                        System.out.println("Value type: " + kvp.getValueType());
                        System.out.print("Values: ");
                        for (byte[] val : kvp.getValues()) {
                            System.out.print(new String(val, "ASCII") + " ");
                        }
                        System.out.println();
                        System.out.println("------------");
                        break;
                }
            }
        }
        System.out.println("2024 연구과제 레디스 dump.rdb 스냅샷 Binary 파일 데이터 조회 프로그램 끝");
    }
}
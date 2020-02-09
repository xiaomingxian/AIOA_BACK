package com.cfcc.modules.elasticsearch.utils;/*
 *
 *
 */

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtils {

    /**
     * 读取pdf文件内容
     * @return
     * @throws IOException
     */
    public static String readPdfFile(String filePath) {
        String result = null;
        FileInputStream is = null;
        PDDocument document = null;
        try {
            is = new FileInputStream(filePath);
            PDFParser parser = new PDFParser(new RandomAccessBuffer(is));
            parser.parse();
            document = parser.getPDDocument();
            PDFTextStripper stripper = new PDFTextStripper();
            result = stripper.getText(document);
            is.close();
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);
        return result;
    }

    /**
     *读取txt文件
     * @param filePath
     * @return
     */
    public static String ReadTxtFile(String filePath){
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "utf-8"));// 构造一个BufferedReader类来读取文件
            String s = null;
            while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
                result = result + "\n" + s;
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);
        return result;
    }

    /**
     * 读取xlsx文件
     * @param filePath
     */
    public static String readxlsxFile(String filePath){
        FileInputStream fis = null;
        Workbook workbook = null;
        List<String> sheetContent = new ArrayList<>();
        try {
            fis = new FileInputStream(filePath);
            workbook = WorkbookFactory.create(fis);
            int sheetCount = workbook.getNumberOfSheets();  //Sheet的数量
            //这里可以遍历每个sheet略
            Sheet sheet = workbook.getSheetAt(0);//第一个sheet
            int rowCount = sheet.getPhysicalNumberOfRows();//总行数
            for (int r = 0; r < rowCount; r++) {
                List<String> rowContent = new ArrayList<>();
                Row row = sheet.getRow(r);
                int cellCount = row.getPhysicalNumberOfCells(); //获取总列数
                for (int c = 0; c < cellCount; c++) {
                    Cell cell = row.getCell(c);
//                    CellType cellType = cell.getCellTypeEnum();
                    String cellValue = null;
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING: //文本
                            cellValue = cell.getStringCellValue();
                            break;
                        case Cell.CELL_TYPE_NUMERIC: //数字、日期numeric
                            if (DateUtil.isCellDateFormatted(cell)) {
                                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                                cellValue = fmt.format(cell.getDateCellValue()); //日期型
                            } else {
                                cellValue = String.valueOf(cell.getNumericCellValue()); //数字
                            }
                            break;
                        case Cell.CELL_TYPE_BOOLEAN: //布尔型
                            cellValue = String.valueOf(cell.getBooleanCellValue());
                            break;
                        default:
                            cellValue = "错误";
                    }
                    rowContent.add(cellValue);
                }
                sheetContent.addAll(rowContent);
//                workbook.close();
                fis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(sheetContent.toString());
        return sheetContent.toString();
    }

    /**
     * 读取xls文件
     * @param filePath
     * @throws Exception
     */
    public static String readXLSFile(String filePath) {
        FileInputStream is = null;
        List<Object> rowContent = new ArrayList<>();
        List<Object> sheetContent = new ArrayList<>();
        try {
            is = new FileInputStream(filePath);
            Workbook workbook = WorkbookFactory.create(is);
            int sheetCount = workbook.getNumberOfSheets();  //Sheet的数量
            //这里可以遍历每个sheet略
            Sheet sheet = workbook.getSheetAt(0);//第一个sheet
            int rowCount = sheet.getPhysicalNumberOfRows();//总行数
            for (int r = 0; r < rowCount; r++) {
                if (r == 0){
                    //不保存第一行的字段名
                    continue;
                }
                Row row = sheet.getRow(r);
                int cellCount = row.getPhysicalNumberOfCells(); //获取总列数
                for (int c = 0; c < cellCount; c++) {
                    Cell cell = row.getCell(c);
//                    CellType cellType = cell.getCellType();
                        Object cellValue = null;
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING: //文本
                            cellValue = cell.getRichStringCellValue();
                            break;
                        case Cell.CELL_TYPE_FORMULA: //数字、日期
                            if (DateUtil.isCellDateFormatted(cell)) {
//                                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
//                                cellValue = fmt.format(cell.getDateCellValue()); //日期型
                                cellValue = cell.getDateCellValue(); //日期型
                            } else {
                                cellValue = String.valueOf(cell.getNumericCellValue()); //数字
                            }
                            break;
                        case Cell.CELL_TYPE_BOOLEAN: //布尔型
                            cellValue = cell.getBooleanCellValue();
                            break;
                        default:  //空白
                            cellValue = "";
                    }
//                System.out.print(cellValue + "    ");
                    rowContent.add(cellValue);
                }
                sheetContent.addAll(rowContent);
            }
//            workbook.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(sheetContent);
        return sheetContent.toString();
    }

    /**
     * 读取docx文件
     * @param filePath
     */
    public static String readDocxFile(String filePath) {
        List<String> contextList = Lists.newArrayList();
        InputStream stream = null;
        try {
            stream = new FileInputStream(new File(filePath));
            XWPFDocument document = new XWPFDocument(stream).getXWPFDocument();
            List<XWPFParagraph> paragraphList = document.getParagraphs();
            paragraphList.forEach(paragraph -> contextList.add(CharMatcher.whitespace().removeFrom(paragraph.getParagraphText())));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != stream) try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
//                LOGGER.debug("读取word文件失败");
                System.out.println("读取word文件失败");
            }
        }
        System.out.println(contextList);
        return contextList.toString();
    }


    /**
     * 读取doc文件
     * @param filePath
     */
    public static String readDocFile(String filePath) {
        List<String> contextList = Lists.newArrayList();
        InputStream stream = null;
        try {
            stream = new FileInputStream(new File(filePath));
            HWPFDocument document = new HWPFDocument(stream);
            WordExtractor extractor = new WordExtractor(document);
            String[] contextArray = extractor.getParagraphText();
            Arrays.asList(contextArray).forEach(context -> contextList.add(CharMatcher.whitespace().removeFrom(context)));
//            extractor.close();
//            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != stream) try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(contextList);
        return contextList.toString();


        //-----------
        /*String str = "";
        try {
            FileInputStream fis = new FileInputStream(new File(filePath));
            HWPFDocument doc = new HWPFDocument(fis);
            String doc1 = doc.getDocumentText();
            System.out.println(doc1);
            StringBuilder doc2 = doc.getText();
            System.out.println(doc2);
            Range rang = doc.getRange();
            String doc3 = rang.text();
            System.out.println(doc3);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return*/
    }






}

package com.lucene.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.*;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;


public class LuceneUtils {

    private static String srcFilePath;
    private static String indexDirPath;
    private static int maxDisplayNums;
    private static int rowStart;
    private static int rowEnd;
    private static int columnCount;

    static {
        Properties p = new Properties();
        try {
            System.out.println("-------------读取配置文件中------------------");
            InputStreamReader isr = new InputStreamReader(new FileInputStream("src/main/resources/conf.properties"), "UTF-8");
            p.load(isr);
            srcFilePath = p.getProperty("srcFilePath");
            indexDirPath = p.getProperty("indexDirPath");
            maxDisplayNums = Integer.parseInt(p.getProperty("maxDisplayNums"));
            rowEnd = Integer.parseInt(p.getProperty("rowEnd"));
            rowStart = Integer.parseInt(p.getProperty("rowStart"));
            columnCount = Integer.parseInt(p.getProperty("columnCount"));
            System.out.println("-------------读取完成------------------");
            System.out.println("-------------src文件地址为:" + srcFilePath);
            System.out.println("-------------index文件夹地址为:" + indexDirPath);
            System.out.println("-------------请确保地址正确,否则将无法运行");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createIndexFromExcel(Integer rowStart, Integer rowEnd, Integer columnCount) throws Exception {
        System.out.println("开始建立索引,耗时依据各电脑性能而定,请耐心等待.........");
        File srcfile = new File(srcFilePath);
        IKAnalyzer analyzer = new IKAnalyzer();
        // 初始化IndexWriter
        List<ExcelSheetPO> excelSheetPOS = ExcelUtil.readExcel(srcfile, rowStart, rowEnd, columnCount);
        FSDirectory indexDir = FSDirectory.open(new File(indexDirPath));
        IndexWriter indexWriter = new IndexWriter(indexDir, analyzer, IndexWriter.MaxFieldLength.UNLIMITED);
        ExcelSheetPO excelSheetPO = excelSheetPOS.get(0);
        ArrayList<ArrayList<Object>> list = excelSheetPO.getDataList();
        for (ArrayList<Object> records : list) {
            Document document = new Document();
            // StringField 对应的值不分词，TextField分词
            Field nameField = new Field("name", ((String) records.get(0) != null ? (String) records.get(0) : ""), Field.Store.YES, Field.Index.ANALYZED);
            Field contentField = new Field("score", ((String) records.get(1) != null ? (String) records.get(1) : ""), Field.Store.YES, Field.Index.ANALYZED);
            Field scoreField = new Field("scoreNums", ((String) records.get(2) != null ? (String) records.get(2) : ""), Field.Store.YES, Field.Index.ANALYZED);
            Field urlField = new Field("url", ((String) records.get(3) != null ? (String) records.get(3) : ""), Field.Store.YES, Field.Index.ANALYZED);
            Field timeField = new Field("time", ((String) records.get(4) != null ? (String) records.get(4) : ""), Field.Store.YES, Field.Index.ANALYZED);
            Field actorsField = new Field("actors", ((String) records.get(5) != null ? (String) records.get(5) : ""), Field.Store.YES, Field.Index.ANALYZED);
            document.add(nameField);
            document.add(contentField);
            document.add(scoreField);
            document.add(urlField);
            document.add(timeField);
            document.add(actorsField);
            indexWriter.addDocument(document);
        }
        indexWriter.commit();
        indexWriter.close();
        System.out.println("索引添加完成");
    }

    public static ScoreDoc[] searchIndex(String txt) throws Exception {
        System.out.println("正在查询中.....");
        String[] fields = {"name","actors", "score", "scoreNums", "url", "time"};
        QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_CURRENT, fields, new IKAnalyzer());
        Query query = queryParser.parse(txt);
//        TermQuery termQuery = new TermQuery(new Term("actors" ,txt));
        IndexReader reader = IndexReader.open(FSDirectory.open(new File(indexDirPath)));
        System.out.println("lucene库中总计文档数:" + reader.numDocs());
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs search = searcher.search(query, 10);
        System.out.println("搜索  ( " + txt + " )  击中数:" + search.totalHits);
        ScoreDoc[] scoreDocs = search.scoreDocs;
        int i = 1;
        for (ScoreDoc scoreDoc : scoreDocs) {
            if (i > maxDisplayNums) {
                System.out.println("查询条数超过最大显示数.................");
                break;
            }
            Document doc = searcher.doc(scoreDoc.doc);
            String name = doc.get("name");
            String score = doc.get("score");
            String scoreNums = doc.get("scoreNums");
            String url = doc.get("url");
            String time = doc.get("time");
            String actors = doc.get("actors");
            System.out.println("i:  " + "电影名:" + name + " 评分:" + score + "    评分人数:" + scoreNums + "  url:" + url + " 时间:" + time + " 演员表:" + actors);
            i++;
        }
        System.out.println("查询完毕");
        return scoreDocs;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("输入  add ,读取数据");
        System.out.println("输入  search ,搜索数据,默认显示最相关的10条");
        System.out.println("输入  exit ,退出程序");

        while (true) {
            System.out.println("请输入命令:");
            Scanner sc = new Scanner(System.in);
            String next = sc.next();
            if ("add".equalsIgnoreCase(next.trim())){
                 createIndexFromExcel(rowStart,rowEnd,columnCount);
            }else if("search".equalsIgnoreCase(next.trim())){
                System.out.println("请输入要查询的内容");
                String txt = sc.next();
                searchIndex(txt.trim());
                continue;
            }else if("exit".equalsIgnoreCase(next.trim())){
                break;
            }
        }

    }
}
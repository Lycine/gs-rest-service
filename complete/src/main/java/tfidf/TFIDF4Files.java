package tfidf;

import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.*;
import java.util.*;

public class TFIDF4Files {

    /**
     * @param args
     */
    private static ArrayList<String> FileList = new ArrayList<String>(); // the list of file

    //get list of file for the directory, including sub-directory of it
    public static List<String> readDirs(String filepath) throws FileNotFoundException, IOException
    {
        try
        {
            File file = new File(filepath);
            if(!file.isDirectory())
            {
                System.out.println("输入的[]");
                System.out.println("filepath:" + file.getAbsolutePath());
            }
            else
            {
                String[] flist = file.list();
                for(int i = 0; i < flist.length; i++)
                {
                    //win
//                    File newfile = new File(filepath + "\\" + flist[i]);
                    //mac/linux
                    File newfile = new File(filepath + "/" + flist[i]);
                    if(!newfile.isDirectory())
                    {
                        FileList.add(newfile.getAbsolutePath());
                    }
                    else if(newfile.isDirectory()) //if file is a directory, call ReadDirs
                    {
                        //win
//                        readDirs(filepath + "\\" + flist[i]);
                        //mac/linux
                        readDirs(filepath + "/" + flist[i]);
                    }
                }
            }
        }catch(FileNotFoundException e)
        {
            System.out.println(e.getMessage());
        }
        return FileList;
    }

    //read file
    public static String readFile(String file) throws FileNotFoundException, IOException
    {
        StringBuffer strSb = new StringBuffer(); //String is constant， StringBuffer can be changed.
        InputStreamReader inStrR = new InputStreamReader(new FileInputStream(file), "utf-8"); //byte streams to character streams
        BufferedReader br = new BufferedReader(inStrR);
        String line = br.readLine();
        while(line != null){
            strSb.append(line).append("\r\n");
            line = br.readLine();
        }

        return strSb.toString();
    }

    //word segmentation
    public static ArrayList<String> cutWords(String file) throws IOException{

        ArrayList<String> words = new ArrayList<String>();
        String text = TFIDF4Files.readFile(file);
        IKAnalyzer analyzer = new IKAnalyzer();
        words = analyzer.split(text);

        return words;
    }

    //term frequency in a file, times for each word
    public static HashMap<String, Integer> normalTF(ArrayList<String> cutwords){
        HashMap<String, Integer> resTF = new HashMap<String, Integer>();
        System.out.println("");
        System.out.print("所有文件分词：");
        for(String word : cutwords){
            if(resTF.get(word) == null){
                resTF.put(word, 1);
                System.out.print("bb: "+word + ", ");
            }
            else{
                resTF.put(word, resTF.get(word) + 1);
                System.out.print("bc: "+word.toString()+", ");
            }
        }

        return resTF;
    }

    //term frequency in a file, frequency of each word
    public static HashMap<String, Float> tf(ArrayList<String> cutwords){
        HashMap<String, Float> resTF = new HashMap<String, Float>();

        int wordLen = cutwords.size();
        HashMap<String, Integer> intTF = TFIDF4Files.normalTF(cutwords);
        System.out.println("频度： ");
        Iterator iter = intTF.entrySet().iterator(); //iterator for that get from TF
        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry)iter.next();
            resTF.put(entry.getKey().toString(), Float.parseFloat(entry.getValue().toString()) / wordLen);
            System.out.print("cc: "+entry.getKey().toString() + " = "+  Float.parseFloat(entry.getValue().toString()) / wordLen+",\t");
        }
        return resTF;
    }

    //tf times for file
    public static HashMap<String, HashMap<String, Integer>> normalTFAllFiles(String dirc) throws IOException{
        HashMap<String, HashMap<String, Integer>> allNormalTF = new HashMap<String, HashMap<String,Integer>>();

        List<String> filelist = TFIDF4Files.readDirs(dirc);
        for(String file : filelist){
            HashMap<String, Integer> dict = new HashMap<String, Integer>();
            ArrayList<String> cutwords = TFIDF4Files.cutWords(file); //get cut word for one file

            dict = TFIDF4Files.normalTF(cutwords);
            allNormalTF.put(file, dict);
        }
        return allNormalTF;
    }

    //tf for all file
    public static HashMap<String,HashMap<String, Float>> tfAllFiles(String dirc) throws IOException{
        HashMap<String, HashMap<String, Float>> allTF = new HashMap<String, HashMap<String, Float>>();
        List<String> filelist = TFIDF4Files.readDirs(dirc);

        for(String file : filelist){
            HashMap<String, Float> dict = new HashMap<String, Float>();
            ArrayList<String> cutwords = TFIDF4Files.cutWords(file); //get cut words for one file

            dict = TFIDF4Files.tf(cutwords);
            allTF.put(file, dict);
        }
        return allTF;
    }
    public static HashMap<String, Float> idf(HashMap<String,HashMap<String, Float>> all_tf){
        System.out.println("all_tf label: " + all_tf.isEmpty());
        HashMap<String, Float> resIdf = new HashMap<String, Float>();
        HashMap<String, Integer> dict = new HashMap<String, Integer>();
        int docNum = FileList.size();
        for(int i = 0; i < docNum; i++){
            HashMap<String, Float> temp = all_tf.get(FileList.get(i));
            Iterator iter = temp.entrySet().iterator();
            while(iter.hasNext()){
                Map.Entry entry = (Map.Entry)iter.next();
                String word = entry.getKey().toString();
                if(dict.get(word) == null){
                    dict.put(word, 1);
                }else {
                    dict.put(word, dict.get(word) + 1);
                }
            }
        }
        System.out.println("IDF for every word is(不同词的权值):");
        Iterator iter_dict = dict.entrySet().iterator();

        while(iter_dict.hasNext()){
            Map.Entry entry = (Map.Entry)iter_dict.next();
            float value = (float)Math.log(docNum / Float.parseFloat(entry.getValue().toString()));
            resIdf.put(entry.getKey().toString(), value);
            System.out.print("dd: " + entry.getKey().toString() + " = " + value+", ");
        }
        System.out.println("");
        return resIdf;
    }
    public static List<String> tf_idf(HashMap<String,HashMap<String, Float>> all_tf,HashMap<String, Float> idfs,float rate){
        HashMap<String, HashMap<String, Float>> resTfIdf = new HashMap<String, HashMap<String, Float>>();

        int docNum = FileList.size();
        for(int i = 0; i < docNum; i++){
            String filepath = FileList.get(i);
            HashMap<String, Float> tfidf = new HashMap<String, Float>();
            HashMap<String, Float> temp = all_tf.get(filepath);
            Iterator iter = temp.entrySet().iterator();
            while(iter.hasNext()){
                Map.Entry entry = (Map.Entry)iter.next();
                String word = entry.getKey().toString();
                Float value = (float)Float.parseFloat(entry.getValue().toString()) * idfs.get(word);
                tfidf.put(word, value);
            }
            resTfIdf.put(filepath, tfidf);
        }
        System.out.println("TF-IDF for Every file is :");
        DisTfIdf(resTfIdf);

        List<String> reswords = FilterTfIdf(resTfIdf, rate);

        return reswords;
    }
    public static void DisTfIdf(HashMap<String, HashMap<String, Float>> tfidf){
        Iterator iter1 = tfidf.entrySet().iterator();
        while(iter1.hasNext()){
            Map.Entry entrys = (Map.Entry)iter1.next();
            System.out.println("FileName: " + entrys.getKey().toString());
            System.out.print("{");
            HashMap<String, Float> temp = (HashMap<String, Float>) entrys.getValue();
            Iterator iter2 = temp.entrySet().iterator();
            while(iter2.hasNext()){
                Map.Entry entry = (Map.Entry)iter2.next();
                System.out.print(entry.getKey().toString() + " = " + entry.getValue().toString() + ", ");
            }
            System.out.println("}");
        }

    }


    /**
     * 筛选出特征明显的词
     * rate 为比率
     * @param tfidf
     */

    public static List<String> FilterTfIdf(HashMap<String, HashMap<String, Float>> tfidf,float rate){
        List<String> listWords = new ArrayList<String>();
        Iterator iter1 = tfidf.entrySet().iterator();
        while(iter1.hasNext()){
            Map.Entry entrys = (Map.Entry)iter1.next();
            System.out.println("FileName: " + entrys.getKey().toString());
            System.out.print("{");

            HashMap<String, Float> temp = (HashMap<String, Float>) entrys.getValue();

            int size = (int)((float)temp.size()*rate);
            int count = 0;

            Iterator iter2 = temp.entrySet().iterator();
            while(iter2.hasNext()&&count<size){
                count++;
                Map.Entry entry = (Map.Entry)iter2.next();
                System.out.print(entry.getKey().toString() + " = " +
                        entry.getValue().toString() + ", ");

                listWords.add(entry.getKey().toString());
            }
            System.out.println("}");
        }

        return listWords;

    }

    public static ArrayList<ArrayList<String>> getTrainData(String trainFile,List<String> words,String charset) throws Exception{

        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();

        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(new File(trainFile)),charset));

        String line = br.readLine();

        while(line!=null){

            String content = line.split("@")[1];
            String label = line.split("@")[0];
            ArrayList<String> list = new ArrayList<String>();
            for (String word:words){
                if(content.contains(word)){
                    list.add("1");
                }else{
                    list.add("0");
                }
            }

            list.add(label);

            data.add(list);

            line = br.readLine();
        }

        br.close();

        return data;

    }

    public static ArrayList<String> getTestData(List<String> words,String content){

        ArrayList<String> list = new ArrayList<String>();
        for (String word:words){
            if(content.contains(word)){
                list.add("1");
            }else{
                list.add("0");
            }
        }

        return list;

    }


    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        String file = "data4tfidf";

        HashMap<String,HashMap<String, Float>> all_tf = tfAllFiles(file);
        System.out.println();
        HashMap<String, Float> idfs = idf(all_tf);
        System.out.println();
        float rate = (float) 0.2;
        List<String> list = tf_idf(all_tf, idfs,rate);

        System.out.println(list);

    }

}
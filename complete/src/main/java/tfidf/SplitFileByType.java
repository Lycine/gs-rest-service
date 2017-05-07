package tfidf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class SplitFileByType {

	public static void splitIntoFiles(String pathin,String dirout,String charset) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(pathin))));

		String line = br.readLine();

		while(line!=null){

			String label = line.split("@")[0];

			File file = new File(dirout+"/"+label+".txt");

			if(!file.exists()){
				file.createNewFile();
				System.out.println("创建"+file.getAbsolutePath());
			}

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file,true), charset));

			bw.append(line.split("@")[1]+"\n");

			line = br.readLine();

			bw.close();
		}


	}


	public static List<String> getAllLabels(String pathin,String charset) throws Exception{

		List<String> list = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(pathin)), charset));


		String line = br.readLine();

		while(line!=null){

			if(!list.contains(line.split("@")[0])){
				list.add(line.split("@")[0]);
			}

			line=br.readLine();
		}


		return list;




	}

	public static void main(String[] args) {
		String pathin = "data/data.txt";
		String dirout = "data4tfidf";
		String charset = "utf-8";
		try {
			List<String> list = getAllLabels(pathin,charset);
			splitIntoFiles(pathin, dirout,charset);
			System.out.println("aa: " + list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

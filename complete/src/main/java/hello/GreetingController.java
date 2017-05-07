package hello;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import tfidf.TFIDF4Files;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name, String symptom, ServletRequest req, ServletResponse res) {
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        System.out.println("symptom: " + symptom);
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    @RequestMapping(value = "/getSymptom", produces = "application/json")
    @ResponseBody
    public Object getSymptom(String symptom, ServletRequest req, ServletResponse res) {


            HttpServletResponse response = (HttpServletResponse) res;
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
            System.out.println("symptom: " + symptom);

            HashMap<String,String > result = new HashMap<>();

            // TODO Auto-generated method stub

            TFIDF4Files tfidf4Files = new TFIDF4Files();

            String file = "/Users/hongyu/IDEAProjects/gs-rest-service/complete/data4tfidf";

            HashMap<String, HashMap<String, Float>> all_tf = null;
            try {
                all_tf = tfidf4Files.tfAllFiles(file);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println();
            HashMap<String, Float> idfs = tfidf4Files.idf(all_tf);
            System.out.println();
            float rate = (float) 0.2;
            List<String> list = tfidf4Files.tf_idf(all_tf, idfs, rate);

            String trainFile = "/Users/hongyu/IDEAProjects/gs-rest-service/complete/data/data.txt";

            try {
                ArrayList<ArrayList<String>> data = tfidf4Files.getTrainData(trainFile, list, "utf-8");

                String content = symptom;

                ArrayList<String> test = tfidf4Files.getTestData(list, content);

                bayes.BayesImpl bayes = new bayes.BayesImpl();

                String label = bayes.predictClass(data, test);

                System.out.println("label: " + label);
                result.put("status","success");
                result.put("content",label);


            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                result.put("status","error");
                result.put("content","未知错误");
            }


        return result;
    }

}

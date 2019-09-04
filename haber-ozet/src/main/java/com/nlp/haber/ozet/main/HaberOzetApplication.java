package com.nlp.haber.ozet.main;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import zemberek.core.logging.Log;
import zemberek.morphology.TurkishMorphology;
import zemberek.ner.NerDataSet;
import zemberek.ner.PerceptronNer;
import zemberek.ner.PerceptronNerTrainer;
import zemberek.ner.NerDataSet.AnnotationStyle;

@Configuration
@EnableAutoConfiguration
@ComponentScan({"controller","service"})
//@SpringBootApplication
public class HaberOzetApplication {

	public static List<String> paths=new ArrayList<String>();
	public static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(HaberOzetApplication.class, args);
	/*	
		 Path trainPath = Paths.get("src/main/resources/ner-train.txt");
		    Path testPath = Paths.get("src/main/resources/ner-test.txt");
		    Path modelRoot = Paths.get("my-model");

		    NerDataSet trainingSet;
			try {
				trainingSet = NerDataSet.load(trainPath, AnnotationStyle.ENAMEX);
				  Log.info(trainingSet.info()); 

				    NerDataSet testSet = NerDataSet.load(testPath, AnnotationStyle.ENAMEX);
				    Log.info(testSet.info());

				    TurkishMorphology morphology = TurkishMorphology.createWithDefaults();
				    PerceptronNer ner = new PerceptronNerTrainer(morphology)
				        .train(trainingSet, testSet, 13, 0.1f);

				    Files.createDirectories(modelRoot);
				    ner.saveModelAsText(modelRoot);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try (Stream<Path> walk = Files.walk(Paths.get("my-model"))) {

				List<String> result = walk.map(x -> x.toString())
						.filter(f -> f.endsWith(".model")).collect(Collectors.toList());

				//result.forEach(System.out::println);
				for (String p : result) {
					p=p.replace("\\", "/");
					String text=readFile(p, Charset.forName("UTF8"));
					text = StringUtils.replace(text, ",", ".");
					IOUtils.write(text, new FileOutputStream(p),  Charset.forName("UTF8"));
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		  */
		    
	}

}

package org.ea;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Parallelized.class)
public class TestInput {
    private String filename = null;

    public TestInput(final String filename) {
        this.filename = filename;
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}

    @AfterClass
    public static void tareDown() {}

    @Parameterized.Parameters(name = "Run #{index}: {0}")
    public static List<String> data() {
        ClassLoader classLoader = TestInput.class.getClassLoader();
        File resultDir = new File(classLoader.getResource("result").getFile());
        List<String> filenames = new ArrayList<String>();
        for (File res : resultDir.listFiles()) {
            filenames.add(res.getName());
        }

        Collections.sort(filenames);
        return filenames;
    }

    @Test
    public void testResults() throws InterruptedException {
        ClassLoader classLoader = TestInput.class.getClassLoader();
        File inputDir = new File(classLoader.getResource("input").getFile());
        File result = new File(classLoader.getResource("result").getFile(), this.filename);
        File input = new File(inputDir, result.getName().substring(0, result.getName().length() - 4) + ".md");

        String inputStr = usingBufferedReader(input.getAbsolutePath());
        String resultStr = usingBufferedReader(result.getAbsolutePath());

        MarkdownToHTML markdownToHTML = new MarkdownToHTML();

        String workStr = markdownToHTML.convertMarkdown(inputStr);

        Thread.sleep(5000);

        assertEquals("The data should be equal", resultStr.trim(), workStr.trim());
    }

    private static String usingBufferedReader(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
}

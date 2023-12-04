package io.github.pmckeown.dependencytrack.modifybom;

import io.github.pmckeown.dependencytrack.bom.SbomComponent;
import io.github.pmckeown.util.Logger;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.*;
import java.util.Scanner;

import static junit.framework.Assert.assertTrue;

public class ModifyBomActionTest {

    @Mock
    private Logger logger;
    private ModifyBomAction action;
    private String bomLoc;
    private String bomWithoutComponents = "{\n" +
            "  \"bomFormat\" : \"CycloneDX\",\n" +
            "  \"specVersion\" : \"1.4\",\n" +
            "  \"serialNumber\" : \"urn:uuid:1bec1d77-61a6-4335-ba79-0cf7df0ce8ba\",\n" +
            "\"version\" : 1,\n" +
            "    \"component\" : {\n" +
            "      \"group\" : \"com.saabgroup.tacticall\",\n" +
            "      \"name\" : \"tacticall\",\n" +
            "      \"version\" : \"5.5-SNAPSHOT\",\n" +
            "      \"licenses\" : [ ],\n" +
            "      \"purl\" : \"pkg:maven/com.saabgroup.tacticall/tacticall@5.5-SNAPSHOT?type=pom\",\n" +
            "      \"type\" : \"library\",\n" +
            "      \"bom-ref\" : \"pkg:maven/com.saabgroup.tacticall/tacticall@5.5-SNAPSHOT?type=pom\"\n" +
            "    },\n" +
            "  \"components\" : [\n" +
            "\t\t  ]\n" +
            "}";

    private String bomWithAddedComponent = "{\n" +
            "\"bomFormat\" : \"CycloneDX\",\n" +
            "\"specVersion\" : \"1.4\",\n" +
            "\"serialNumber\" : \"urn:uuid:1bec1d77-61a6-4335-ba79-0cf7df0ce8ba\",\n" +
            "\"version\" : 1,\n" +
            "\"component\" : {\n" +
            "\"group\" : \"com.saabgroup.tacticall\",\n" +
            "\"name\" : \"tacticall\",\n" +
            "\"version\" : \"5.5-SNAPSHOT\",\n" +
            "\"licenses\" : [ ],\n" +
            "\"purl\" : \"pkg:maven/com.saabgroup.tacticall/tacticall@5.5-SNAPSHOT?type=pom\",\n" +
            "\"type\" : \"library\",\n" +
            "\"bom-ref\" : \"pkg:maven/com.saabgroup.tacticall/tacticall@5.5-SNAPSHOT?type=pom\"\n" +
            "},\n" +
            "\"components\" : [\n" +
            "\t\t{\n" +
            "\t\t\"group\" : \"com.saabgroup.tacticall.base\",\n" +
            "\t\t\"name\" : \"tacticall-loglayout\",\n" +
            "\t\t\"version\" : \"1.0.0+20230925-9b7d0ff\",\n" +
            "\t       \t\"cpe\" : \"pkg:maven/com.saabgroup.tacticall.base/tacticall-loglayout@1.0.0%2B20230925-9b7d0ff?type=jar\",\n" +
            "\t\t\"type\" : \"library\",\n" +
            "\t\t\"bom-ref\" : \"pkg:maven/com.saabgroup.tacticall.base/tacticall-loglayout@1.0.0%2B20230925-9b7d0ff?type=jar\"\n" +
            "\t\t}\n" +
            "\t\t]\n" +
            "}";


    @Before
    public void setUp(){
        action = new ModifyBomAction(logger);
        bomLoc = System.getProperty("user.dir") + "test-bom.json";
    }

    @After
    public void tearDown() {
        File bomFile = new File(bomLoc);
        bomFile.delete();
    }


    @Test
    public void componentAddedInCorrectArray() throws FileNotFoundException {
        JSONObject bom = new JSONObject(bomWithoutComponents);
        SbomComponent component = setUpComponent();

        //Do stuff
        JSONObject modifiedBom = action.addComponent(bom, component);


        //Assert
        JSONObject expectedResult = new JSONObject(bomWithAddedComponent);

        assertTrue(expectedResult.similar(modifiedBom));
    }


    @Test
    public void addsNothingWhenComponentArrayIsEmpty() throws IOException {
        //Set up
        setUpBOM();

        //Do stuff
        boolean success = action.addComponents(bomLoc, new SbomComponent[0]);

        //assert
        assertTrue(success);
        assertSimilar(bomWithoutComponents, fileToString(bomLoc));

    }

    @Test
    public void addsASingleComponent() throws IOException {
        //Set up
        setUpBOM();
        SbomComponent component = setUpComponent();

        SbomComponent[] components = new SbomComponent[1];
        components[0] = component;

        //Do stuff
        action.addComponents(bomLoc, components);

        System.out.println(bomWithAddedComponent);
        System.out.println(fileToString(bomLoc));
        //Assert
        assertSimilar(fileToString(bomLoc), bomWithAddedComponent);
    }

    private SbomComponent setUpComponent() {
        SbomComponent component = new SbomComponent();
        component.setGroup("com.saabgroup.tacticall.base");
        component.setName("tacticall-loglayout");
        component.setVersion("1.0.0+20230925-9b7d0ff");
        component.setCpe("pkg:maven/com.saabgroup.tacticall.base/tacticall-loglayout@1.0.0%2B20230925-9b7d0ff?type=jar");
        component.setType("library");
        component.setBomRef("pkg:maven/com.saabgroup.tacticall.base/tacticall-loglayout@1.0.0%2B20230925-9b7d0ff?type=jar");
        return component;
    }

    private String fileToString (String location) throws FileNotFoundException {
        File file = new File(location);
        Scanner reader = new Scanner(file);
        StringBuilder stringBuilder = new StringBuilder();
        while (reader.hasNext()){
            stringBuilder.append(reader.nextLine());
            if (reader.hasNext()) {
                stringBuilder.append("\n");
            }
        }
        reader.close();

        return stringBuilder.toString();
    }

    private void setUpBOM() throws IOException {
        File bomFile = new File(bomLoc);
        bomFile.delete();
        bomFile.createNewFile();


        BufferedWriter writer = new BufferedWriter(new FileWriter(bomLoc));
        writer.write(bomWithoutComponents);
        writer.close();
    }

    private void assertSimilar(String s1, String s2){
        JSONObject j1 = new JSONObject(s1);
        JSONObject j2 = new JSONObject(s2);

        assertTrue(j1.similar(j2));
    }
}
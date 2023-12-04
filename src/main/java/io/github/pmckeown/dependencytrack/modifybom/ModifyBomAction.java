package io.github.pmckeown.dependencytrack.modifybom;

import com.google.gson.Gson;
import io.github.pmckeown.dependencytrack.bom.SbomComponent;
import io.github.pmckeown.util.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.util.Scanner;

@Singleton
public class ModifyBomAction {

    private final String COMPONENTS_ARRAY = "components";
    private String bomLocation;
    private Logger logger;

    @Inject
    public ModifyBomAction(Logger logger){
        this.logger = logger;
    }

    public boolean addComponents(String bomLocation, SbomComponent[] components) {
        this.bomLocation = bomLocation;

        if (components.length == 0) return true;
        try {
            JSONObject bom = getBomJSON(bomLocation);

            for (SbomComponent component : components) {
                bom = addComponent(bom, component);
            }

            writeBomJSONToFile(bomLocation, bom);

        } catch (IOException e) {
            return false;
        }

        return true;
    }


    public JSONObject addComponent(JSONObject bom, SbomComponent component) throws FileNotFoundException {
        JSONArray components = bom.getJSONArray(COMPONENTS_ARRAY);
        JSONObject jsonComponent = component.serialize();

        //put component into array and array into bom
        components.put(jsonComponent);
        bom.put(COMPONENTS_ARRAY,components);

        return bom;
    }



    private JSONObject getBomJSON (String bomLocation) throws FileNotFoundException{
        File bomFile = new File(bomLocation);
        Scanner bomReader = new Scanner(bomFile);
        StringBuilder bomStringBuilder = new StringBuilder();
        while (bomReader.hasNext()){
            bomStringBuilder.append(bomReader.nextLine());
        }
        bomReader.close();
        return new JSONObject(bomStringBuilder.toString());
    }


    private void writeBomJSONToFile(String location, JSONObject bom) throws IOException {
        File bomFile = new File(location);
        BufferedWriter writer = new BufferedWriter(new FileWriter(bomFile));
        writer.write(bom.toString());
        writer.close();
    }

}

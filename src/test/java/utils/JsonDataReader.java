package utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonDataReader {

    public static <T> Object[][] getTestData(String filePath, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File("src/test/resources/" + filePath);

            List<T> cases = mapper.readValue(
                    file,
                    mapper.getTypeFactory().constructCollectionType(List.class, clazz)
            );

            Object[][] data = new Object[cases.size()][1];
            for (int i = 0; i < cases.size(); i++) {
                data[i][0] = cases.get(i);
            }
            return data;

        } catch (IOException e) {
            throw new RuntimeException("Failed to read test data file: " + filePath, e);
        }
    }
}
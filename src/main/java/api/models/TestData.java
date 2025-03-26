package api.models;

import api.data.TestDataGenerator;
import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

@Data
public class TestData {
    private Project project;
    private User user;
    private BuildType buildType;

    public static TestData generate() {
        // Идем по всем полям TestData и для каждого, кто наследник BaseModel вызывыем generate() c передачей уже сгенерированных сущностей
        try {
            var instance = TestData.class.getDeclaredConstructor().newInstance();
            var generatedModels = new ArrayList<BaseModel>();
            for (var field: TestData.class.getDeclaredFields()) {
                field.setAccessible(true);
                if (BaseModel.class.isAssignableFrom(field.getType())) {
                    var generatedModel = TestDataGenerator.generate(generatedModels, field.getType().asSubclass(BaseModel.class));
                    field.set(instance, generatedModel);
                    generatedModels.add(generatedModel);
                }
                field.setAccessible(false);
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException("Cannot generate test data", e);
        }
    }
}

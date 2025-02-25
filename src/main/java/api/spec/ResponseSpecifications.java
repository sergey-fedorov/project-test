package api.spec;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

public class ResponseSpecifications {

    public static ResponseSpecification checkProjectNotFoundById(String projectId) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_NOT_FOUND);
        responseSpecBuilder.expectBody(Matchers.containsString("Project cannot be found by external id '%s'".formatted(projectId)));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkProjectWithNameAlreadyExist(String projectName) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_BAD_REQUEST);
        responseSpecBuilder.expectBody(Matchers.containsString("Project with this name already exists: %s".formatted(projectName)));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkProjectCannotBeEmpty() {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_BAD_REQUEST);
        responseSpecBuilder.expectBody(Matchers.containsString("Project name cannot be empty."));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkProjectIdIsInvalid(String projectId) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        responseSpecBuilder.expectBody(Matchers.containsString("Project ID \"%s\" is invalid: starts with non-letter character '%s'.".formatted(projectId, projectId.charAt(0))));
        return responseSpecBuilder.build();
    }
}

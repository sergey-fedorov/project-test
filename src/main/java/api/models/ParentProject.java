package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentProject {
    private String locator;
    private String id;
    private String name;
    private String description;
    private String href;
    private String webUrl;
}

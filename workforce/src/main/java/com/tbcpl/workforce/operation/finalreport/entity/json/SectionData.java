package com.tbcpl.workforce.operation.finalreport.entity.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SectionData {

    private String id;
    private String title;
    private String type;         // "table" | "custom-table" | "narrative"
    private Object content;      // deserialized based on type at runtime
    private List<String> images; // Cloudinary URLs
    private String notes;
    private String notesHeading;
}

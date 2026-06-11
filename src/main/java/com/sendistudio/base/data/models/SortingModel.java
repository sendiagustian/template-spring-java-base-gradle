package com.sendistudio.base.data.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SortingModel {
    private String sortBy;
    private String sortDirection; // ASC or DESC
    
    /**
     * Check if sorting is active
     */
    public boolean isActive() {
        return sortBy != null && !sortBy.trim().isEmpty();
    }
    
    /**
     * Get validated sort direction (defaults to ASC if invalid)
     */
    public String getValidatedDirection() {
        if (sortDirection == null) return "ASC";
        String upper = sortDirection.trim().toUpperCase();
        return upper.equals("DESC") ? "DESC" : "ASC";
    }
}

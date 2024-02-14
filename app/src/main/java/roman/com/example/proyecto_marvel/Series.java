package roman.com.example.proyecto_marvel;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "Series")
public class Series {
    @PrimaryKey(autoGenerate = true)
    public int id;
    String title;
    String description;
    String resourceURI;
    int startYear;
    int endYear;
    String urlImagen;
    int storyAvailable;
    int storyReturned;


    public Series(int id, String title, String description, String resourceURI, int startYear, int endYear, String urlImagen, int storyAvailable, int storyReturned) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.resourceURI = resourceURI;
        this.startYear = startYear;
        this.endYear = endYear;
        this.urlImagen = urlImagen;
        this.storyAvailable = storyAvailable;
        this.storyReturned = storyReturned;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getResourceURI() {
        return resourceURI;
    }

    public int getStartYear() {
        return startYear;
    }

    public int getEndYear() {
        return endYear;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public int getStoryAvailable() {
        return storyAvailable;
    }

    public int getStoryReturned() {
        return storyReturned;
    }


}


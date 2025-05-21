package models;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "data")
@XmlAccessorType(XmlAccessType.FIELD)
public class FilmWrapper {
    @XmlElement(name = "item")
    private List<Film> films;

    public FilmWrapper() {
    }

    public FilmWrapper(List<Film> films) {
        this.films = films;
    }

    public List<Film> getFilms() {
        return films;
    }

    public void setFilms(List<Film> films) {
        this.films = films;
    }
}
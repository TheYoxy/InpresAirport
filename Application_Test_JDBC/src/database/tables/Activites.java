package database.tables;

public class Activites {

    private final String Cours;
    private final String Type;
    private final String Date;
    private String Description;
    private String Reference;

    public Activites() {
        Cours = "";
        Type = "";
        Date = "";
    }

    public Activites(String cours, String type, String date, String desc, String ref) {
        Cours = cours;
        Type = type;
        Date = date;
        Description = desc;
        Reference = ref;
    }

    public String getCours() {
        return Cours;
    }

    public String getType() {
        return Type;
    }

    public String getDate() {
        return Date;
    }

    public String getDescription() {
        return Description;
    }

    public String getReference() {
        return Reference;
    }
}


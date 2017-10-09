package database.tables;

public class Activites {

        private String _cours;
        private String _type;
        private String _date;
        private String _description;
        private String _reference;

        public Activites(){
            _cours = "";
            _type = "";
            _date = "";
        }

        public Activites(String cours, String type, String date, String desc, String ref) {
            _cours = cours;
            _type = type;
            _date = date;
            _description = desc;
            _reference = ref;
        }

        public String get_cours() {
            return _cours;
        }

        public String get_type() { return _type; }

        public String get_date() { return _date; }

        public String get_description() { return _description; }

        public String get_reference() { return _reference; }
}


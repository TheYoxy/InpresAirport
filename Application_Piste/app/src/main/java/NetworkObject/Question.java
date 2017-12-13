package NetworkObject;

public class Question {
    private int nbQuestion;
    private String question;
    private String userFrom;
    private byte[] digest;

    public Question() {
    }

    public Question(int nbQuestion) {
        this.nbQuestion = nbQuestion;
    }

    public Question(int nbQuestion, String question, String userFrom) {
        this(nbQuestion);
        this.question = question;
        this.userFrom = userFrom;
    }

    public Question(int nbQuestion, String question, String userFrom, byte[] digest) {
        this(nbQuestion, question, userFrom);
        this.digest = digest;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setUserFrom(String userFrom) {
        this.userFrom = userFrom;
    }

    public void setDigest(byte[] digest) {
        this.digest = digest;
    }

    public String getQuestion() {
        return question;
    }

    public String getUserFrom() {
        return userFrom;
    }

    public byte[] getDigest() {
        return digest;
    }
}

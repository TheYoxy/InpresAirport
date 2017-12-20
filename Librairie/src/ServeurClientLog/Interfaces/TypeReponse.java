package ServeurClientLog.Interfaces;

/**
 * Interface servant de flag
 */
public interface TypeReponse {
    static TypeReponse getError() {
        throw new UnsupportedOperationException("Unimplemented method");
    }
}

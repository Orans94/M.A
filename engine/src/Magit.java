import java.util.HashSet;
import java.util.Set;

public class Magit {
    private final Branch Head;
    private Set<Commit> m_Commits = new HashSet<>();
    private Set<Branch> m_Branches = new HashSet<>();


    public Magit(Branch head) {
        Head = head;
    }
}

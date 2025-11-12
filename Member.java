// Member.java
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Member implements Serializable {
    private int memberId;
    private String name;
    private String email;
    private List<Integer> issuedBooks; // list of book IDs

    public Member(int memberId, String name, String email) {
        this.memberId = memberId;
        this.name = name.trim();
        this.email = email.trim();
        this.issuedBooks = new ArrayList<>();
    }

    public int getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<Integer> getIssuedBooks() { return issuedBooks; }

    public void displayMemberDetails() {
        System.out.println("ID: " + memberId + ", Name: " + name + ", Email: " + email);
        System.out.println("Issued Book IDs: " + issuedBooks);
    }

    public void addIssuedBook(int bookId) {
        if (!issuedBooks.contains(bookId)) issuedBooks.add(bookId);
    }

    public void returnIssuedBook(int bookId) {
        issuedBooks.remove(Integer.valueOf(bookId));
    }

    public String toDataString() {
        // memberId|name|email|book1,book2,book3
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < issuedBooks.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(issuedBooks.get(i));
        }
        return memberId + "|" + escape(name) + "|" + escape(email) + "|" + sb.toString();
    }

    public static Member fromDataString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 4) return null;
        try {
            int id = Integer.parseInt(parts[0]);
            String name = unescape(parts[1]);
            String email = unescape(parts[2]);
            Member m = new Member(id, name, email);
            String booksPart = parts[3];
            if (!booksPart.isEmpty()) {
                String[] ids = booksPart.split(",");
                for (String s : ids) {
                    try { m.addIssuedBook(Integer.parseInt(s)); } catch (NumberFormatException ignored) {}
                }
            }
            return m;
        } catch (NumberFormatException e) { return null; }
    }

    private static String escape(String s) { return s.replace("|", "\\|"); }
    private static String unescape(String s) { return s.replace("\\|", "|"); }

    public static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}

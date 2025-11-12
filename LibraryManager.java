// LibraryManager.java
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class LibraryManager {
    private Map<Integer, Book> books = new HashMap<>();
    private Map<Integer, Member> members = new HashMap<>();
    private Set<String> categories = new HashSet<>();
    private int nextBookId = 100;   // starting id
    private int nextMemberId = 200; // starting id
    private static final String BOOK_FILE = "books.txt";
    private static final String MEMBER_FILE = "members.txt";
    private Scanner sc = new Scanner(System.in);
    private Queue<Integer> waitQueue = new LinkedList<>(); // optional waiting list (book id placeholders)

    public LibraryManager() {
        loadFromFile();
    }

    public void menu() {
        while (true) {
            System.out.println("\n===== City Library Digital Management System =====");
            System.out.println("1. Add Book");
            System.out.println("2. Add Member");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. Search Books");
            System.out.println("6. Sort Books");
            System.out.println("7. Show All Books");
            System.out.println("8. Show All Members");
            System.out.println("9. Exit");
            System.out.print("Enter your choice: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": addBook(); break;
                case "2": addMember(); break;
                case "3": issueBook(); break;
                case "4": returnBook(); break;
                case "5": searchBooks(); break;
                case "6": sortBooksMenu(); break;
                case "7": showAllBooks(); break;
                case "8": showAllMembers(); break;
                case "9": saveToFile(); System.out.println("Saved and exiting."); return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    // Add Book
    private void addBook() {
        try {
            System.out.print("Enter Title: ");
            String title = sc.nextLine();
            System.out.print("Enter Author: ");
            String author = sc.nextLine();
            System.out.print("Enter Category: ");
            String category = sc.nextLine();
            int id = nextBookId++;
            Book b = new Book(id, title, author, category);
            books.put(id, b);
            categories.add(category.trim());
            saveToFile();
            System.out.println("Book added successfully with ID: " + id);
        } catch (Exception e) { System.out.println("Error adding book: " + e.getMessage()); }
    }

    // Add Member
    private void addMember() {
        try {
            System.out.print("Enter Name: ");
            String name = sc.nextLine();
            System.out.print("Enter Email: ");
            String email = sc.nextLine();
            if (!Member.isValidEmail(email)) {
                System.out.println("Invalid email format. Member not added.");
                return;
            }
            int id = nextMemberId++;
            Member m = new Member(id, name, email);
            members.put(id, m);
            saveToFile();
            System.out.println("Member added successfully with ID: " + id);
        } catch (Exception e) { System.out.println("Error adding member: " + e.getMessage()); }
    }

    private void issueBook() {
        try {
            System.out.print("Enter Book ID to issue: ");
            int bid = Integer.parseInt(sc.nextLine());
            Book b = books.get(bid);
            if (b == null) { System.out.println("Book not found."); return; }
            if (b.isIssued()) {
                System.out.println("Book already issued.");
                // optional: add to waitQueue
                System.out.print("Add member to waitlist? (y/n): ");
                String r = sc.nextLine().trim();
                if (r.equalsIgnoreCase("y")) {
                    waitQueue.add(bid);
                    System.out.println("Added to waitlist.");
                }
                return;
            }
            System.out.print("Enter Member ID: ");
            int mid = Integer.parseInt(sc.nextLine());
            Member m = members.get(mid);
            if (m == null) { System.out.println("Member not found."); return; }
            b.markAsIssued();
            m.addIssuedBook(bid);
            saveToFile();
            System.out.println("Book issued to member ID: " + mid);
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid number input.");
        }
    }

    private void returnBook() {
        try {
            System.out.print("Enter Book ID to return: ");
            int bid = Integer.parseInt(sc.nextLine());
            Book b = books.get(bid);
            if (b == null) { System.out.println("Book not found."); return; }
            if (!b.isIssued()) { System.out.println("Book is not currently issued."); return; }
            System.out.print("Enter Member ID who is returning: ");
            int mid = Integer.parseInt(sc.nextLine());
            Member m = members.get(mid);
            if (m == null) { System.out.println("Member not found."); return; }
            b.markAsReturned();
            m.returnIssuedBook(bid);
            saveToFile();
            System.out.println("Book returned successfully.");
            // if waitlist exists, notify (simple console)
            if (!waitQueue.isEmpty()) {
                Integer next = waitQueue.poll();
                if (next != null && next == bid) {
                    System.out.println("Book had a waitlist entry. Next in queue should be notified externally.");
                } else if (next != null) {
                    // put back if different
                    waitQueue.add(next);
                }
            }
        } catch (NumberFormatException nfe) { System.out.println("Invalid number input."); }
    }

    // search by title / author / category
    private void searchBooks() {
        System.out.print("Search by (title/author/category): ");
        String by = sc.nextLine().trim().toLowerCase();
        System.out.print("Enter search text: ");
        String q = sc.nextLine().trim().toLowerCase();
        List<Book> results = new ArrayList<>();
        for (Book b : books.values()) {
            if (by.equals("title") && b.getTitle().toLowerCase().contains(q)) results.add(b);
            else if (by.equals("author") && b.getAuthor().toLowerCase().contains(q)) results.add(b);
            else if (by.equals("category") && b.getCategory().toLowerCase().contains(q)) results.add(b);
        }
        if (results.isEmpty()) System.out.println("No results found.");
        else results.forEach(System.out::println);
    }

    // sort by title (Comparable) or by author/category (Comparator)
    private void sortBooksMenu() {
        System.out.println("Sort by: 1) Title 2) Author 3) Category");
        String c = sc.nextLine().trim();
        List<Book> list = new ArrayList<>(books.values());
        switch (c) {
            case "1": Collections.sort(list); break; // Comparable: title
            case "2":
                list.sort(Comparator.comparing(b -> b.getAuthor().toLowerCase()));
                break;
            case "3":
                list.sort(Comparator.comparing(b -> b.getCategory().toLowerCase()));
                break;
            default: System.out.println("Invalid choice."); return;
        }
        list.forEach(System.out::println);
    }

    private void showAllBooks() {
        if (books.isEmpty()) System.out.println("No books in library.");
        else books.values().forEach(System.out::println);
    }

    private void showAllMembers() {
        if (members.isEmpty()) System.out.println("No members.");
        else members.values().forEach(Member::displayMemberDetails);
    }

    // file IO
    public void saveToFile() {
        // save books
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(BOOK_FILE))) {
            for (Book b : books.values()) {
                bw.write(b.toDataString());
                bw.newLine();
            }
        } catch (IOException e) { System.out.println("Error saving books: " + e.getMessage()); }

        // save members
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MEMBER_FILE))) {
            for (Member m : members.values()) {
                bw.write(m.toDataString());
                bw.newLine();
            }
        } catch (IOException e) { System.out.println("Error saving members: " + e.getMessage()); }
    }

    public void loadFromFile() {
        // load books
        File bf = new File(BOOK_FILE);
        if (bf.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(bf))) {
                String line; int maxId = nextBookId;
                while ((line = br.readLine()) != null) {
                    Book b = Book.fromDataString(line);
                    if (b != null) {
                        books.put(b.getBookId(), b);
                        categories.add(b.getCategory());
                        maxId = Math.max(maxId, b.getBookId()+1);
                    }
                }
                nextBookId = Math.max(nextBookId, maxId);
            } catch (IOException e) { System.out.println("Error loading books: " + e.getMessage()); }
        }

        // load members
        File mf = new File(MEMBER_FILE);
        if (mf.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(mf))) {
                String line; int maxId = nextMemberId;
                while ((line = br.readLine()) != null) {
                    Member m = Member.fromDataString(line);
                    if (m != null) {
                        members.put(m.getMemberId(), m);
                        maxId = Math.max(maxId, m.getMemberId()+1);
                    }
                }
                nextMemberId = Math.max(nextMemberId, maxId);
            } catch (IOException e) { System.out.println("Error loading members: " + e.getMessage()); }
        }
    }

    public static void main(String[] args) {
        LibraryManager lm = new LibraryManager();
        lm.menu();
    }
}

# City Library Digital Management System
Java Assignment 4 - ENCS201 / ENCA203 / ENBC205

**Student:** Umang Gupta  
**Roll No:** 2401010001

## Files
- Book.java
- Member.java
- LibraryManager.java

## How to compile & run (local machine with JDK)
1. Download the .java files (or clone the repo).
2. Open terminal in the folder with the files.
3. Compile:
   javac Book.java Member.java LibraryManager.java
4. Run:
   java LibraryManager

Notes:
- The program uses `books.txt` and `members.txt` to store data. If these files do not exist, the program will create them automatically.
- Sorting: Book implements Comparable (title). Additional comparators used for author & category.
- Email validation is basic regex; change if instructor requires different rule.

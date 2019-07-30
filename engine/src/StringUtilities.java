public class StringUtilities {
    //method to cut each row and create the contnet for making sha1
    public static String makeSha1Content(String i_ContentToCut) {
        String newContent = "";
        String[] lines = i_ContentToCut.split(System.lineSeparator());
        for (String line : lines) {
            String[] members = line.split(",");
            newContent = newContent.concat(members[0])
                    .concat(",").concat(members[1])
                    .concat(",").concat(members[2])
                    .concat(System.lineSeparator());
        }
        newContent = newContent.substring(0, newContent.length() - 2);

        return newContent;
    }
}

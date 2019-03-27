package ch.qos.logback.core.rolling;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.Random;

/**
 * Created by p0po on 2019/3/21 0021.
 */
public class MyFixedWindowRollingPolicy extends FixedWindowRollingPolicy {

    Random random = new Random();

    @Override
    public void rollover() throws RolloverFailure {

        String toRenameStr1 = DateTime.now().toString("yyMMddHHmmssSSS");

        // Inside this method it is guaranteed that the hereto active log file is
        // closed.
        // If maxIndex <= 0, then there is no file renaming to be done.
        /*if (maxIndex >= 0) {
            // Delete the oldest file, to keep Windows happy.
            File file = new File(fileNamePattern.convertInt(maxIndex));

            if (file.exists()) {
                file.delete();
            }

            // Map {(maxIndex - 1), ..., minIndex} to {maxIndex, ..., minIndex+1}
            for (int i = maxIndex - 1; i >= minIndex; i--) {
                String toRenameStr = fileNamePattern.convertInt(i);
                File toRename = new File(toRenameStr);
                // no point in trying to rename an inexistent file
                if (toRename.exists()) {
                    util.rename(toRenameStr, fileNamePattern.convertInt(i + 1));
                } else {
                    addInfo("Skipping roll-over for inexistent file " + toRenameStr);
                }
            }*/

        // move active file name to min
        switch (compressionMode) {
            case NONE:
                util.rename(getActiveFileName(), fileNamePattern.convertInt(20) + toRenameStr1);
                break;
            case GZ:
                compressor.compress(getActiveFileName(), fileNamePattern.convertInt(minIndex), null);
                break;
            case ZIP:
                compressor.compress(getActiveFileName(), fileNamePattern.convertInt(minIndex), zipEntryFileNamePattern.convert(new Date()));
                break;
        }
    }

    public static void main(String[] args) {
        String toRenameStr1 = DateTime.now().toString("yyMMddHHmmssSSS");
        System.out.println("args = [" + toRenameStr1 + "]");
    }
}


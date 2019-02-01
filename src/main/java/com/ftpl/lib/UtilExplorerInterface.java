package com.ftpl.lib;

import java.io.IOException;

public interface UtilExplorerInterface {

    boolean cd(String directory);

    String getDir();

    boolean setDir(String dir) throws IOException;
}

package org.openmarl.aitk;

import org.openmarl.libaitk.AitkContext;
import org.openmarl.susrv.LibSusrv;

import java.util.Date;

/**
 * Created by chris on 14/11/14.
 */
public class AitkInjection {

    private String processName;
    private int pid;
    private String injectionLibrary;
    private Date injectionTime;
    private String logPath;

    public AitkInjection(String processName,
                         String injectionLibrary)
    {
        this.processName = processName;
        this.pid = LibSusrv.getpid(this.processName);
        this.injectionLibrary = injectionLibrary;
        this.injectionTime = new Date();
        this.logPath = String.format("%s/%s-%05d.log",
                AitkContext.AITK_LOGS_DIR,
                this.injectionLibrary,
                this.pid);
    }

    public String getTargetName() {
        return processName;
    }

    public int getTargetPid() {
        return pid;
    }

    public String getInjectionLibrary() {
        return injectionLibrary;
    }

    public Date getInjectionTime() {
        return injectionTime;
    }

    public String getLogPath() {
        return logPath;
    }
}

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;


public class ProcessOutputConsumer {

        private InputStream stderr;
        private InputStream stdout;
        private final String command;
        private boolean showOutput;

        public ProcessOutputConsumer(Process process, String command, boolean showOutput) {
            stderr = process.getErrorStream(); //== null ? null : new BufferedInputStream(process.getErrorStream());
            stdout = process.getInputStream(); //== null ? null : new BufferedInputStream(process.getInputStream());
            this.command = command;
            this.showOutput = showOutput;
        }

        public InputStream takeStdout() {
            InputStream ret = stdout;
            stdout = null;
            return ret;
        }

        public InputStream takeStderr() {
            InputStream ret = stderr;
            stderr = null;
            return ret;
        }

        public void start() {
            if (stdout != null || stderr != null) {
                final BufferedInputStream stderrBuf = stderr == null ? null : new BufferedInputStream(stderr);
                final BufferedInputStream stdoutBuf = stdout == null ? null : new BufferedInputStream(stdout);
                new Thread() {

                    @Override
                    public void run() {
                        setName("ProcessOutputConsumer_thread");
                        boolean readStdout = stdoutBuf != null;
                        boolean readStderr = stderrBuf != null;
                        byte[] data = new byte[128];
                        while (readStderr || readStdout) {
                            if (readStderr) {
                                try {
                                    if (stderrBuf.read(data) < 0) {
                                        readStderr = false;// END
                                    }
                                   
                                } catch (IOException ex) {
                                	System.out.println("Error when reading '" + command + "' command error output"+ex.getMessage());
                                    readStderr = false;
                                }
                            }
                            if (readStdout) {
                            	if ( showOutput ){
                            		System.out.print(new String(data));
                            	}
                                try {
                                    if (stdoutBuf.read(data) < 0) {
                                        readStdout = false;// END
                                    }
                                } catch (IOException ex) {
                                    System.out.println( "Error when reading '" + command + "' command standard output" +ex.getMessage());
                                    readStdout = false;
                                }
                            }
                        }
                        try {
							stderrBuf.close();
		                    stdoutBuf.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
   
                    }
                }.start();
            }
        }

    }
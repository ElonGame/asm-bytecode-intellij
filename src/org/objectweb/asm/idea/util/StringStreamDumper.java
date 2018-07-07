package org.objectweb.asm.idea.util;

import org.benf.cfr.reader.entities.Method;
import org.benf.cfr.reader.state.TypeUsageInformation;
import org.benf.cfr.reader.util.getopt.Options;
import org.benf.cfr.reader.util.output.IllegalIdentifierDump;
import org.benf.cfr.reader.util.output.StreamDumper;

import java.io.StringWriter;

/**
 * @author Quding Ding
 * @since 2018/7/7
 */
public class StringStreamDumper extends StreamDumper {
  private final StringWriter writer;

  public StringStreamDumper(StringWriter writer, TypeUsageInformation typeUsageInformation, Options options) {
    super(typeUsageInformation, options, new IllegalIdentifierDump.Nop());
    this.writer = writer;
  }

  protected void write(String s) {
    writer.append(s);
  }

  public void close() {
  }

  public void addSummaryError(Method method, String s) {

  }
}

package org.objectweb.asm.idea.util;

import org.benf.cfr.reader.bytecode.analysis.types.JavaTypeInstance;
import org.benf.cfr.reader.state.TypeUsageInformation;
import org.benf.cfr.reader.util.getopt.Options;
import org.benf.cfr.reader.util.getopt.OptionsImpl;
import org.benf.cfr.reader.util.output.Dumper;
import org.benf.cfr.reader.util.output.DumperFactory;
import org.benf.cfr.reader.util.output.FileSummaryDumper;
import org.benf.cfr.reader.util.output.IllegalIdentifierDump;
import org.benf.cfr.reader.util.output.NopSummaryDumper;
import org.benf.cfr.reader.util.output.ProgressDumper;
import org.benf.cfr.reader.util.output.ProgressDumperNop;
import org.benf.cfr.reader.util.output.ProgressDumperStdErr;
import org.benf.cfr.reader.util.output.SummaryDumper;

import java.io.StringWriter;

/**
 * @author Quding Ding
 * @since 2018/7/7
 */
public class PluginDumperFactory implements DumperFactory {
  private final StringWriter outBuffer;
  private final Options options;
  private final ProgressDumper progressDumper;


  public PluginDumperFactory(StringWriter out, Options options) {
    this.outBuffer = out;
    this.options = options;
    if (options.getOption(OptionsImpl.SILENT) || !options.optionIsSet(OptionsImpl.OUTPUT_DIR) && !options.optionIsSet(OptionsImpl.OUTPUT_PATH)) {
      this.progressDumper = ProgressDumperNop.INSTANCE;
    } else {
      this.progressDumper = new ProgressDumperStdErr();
    }
  }


  @Override
  public Dumper getNewTopLevelDumper(JavaTypeInstance javaTypeInstance, SummaryDumper summaryDumper,
      TypeUsageInformation typeUsageInformation, IllegalIdentifierDump illegalIdentifierDump) {
    return new StringStreamDumper(this.outBuffer, typeUsageInformation, options);
  }

  @Override
  public ProgressDumper getProgressDumper() {
    return progressDumper;
  }

  @Override
  public SummaryDumper getSummaryDumper() {
    return !options.optionIsSet(OptionsImpl.OUTPUT_DIR) ?
        new NopSummaryDumper() : new FileSummaryDumper(options.getOption(OptionsImpl.OUTPUT_DIR), options, null);

  }
}

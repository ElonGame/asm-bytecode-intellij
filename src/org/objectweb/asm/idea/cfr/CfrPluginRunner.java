package org.objectweb.asm.idea.cfr;

import org.benf.cfr.reader.Main;
import org.benf.cfr.reader.api.ClassFileSource;
import org.benf.cfr.reader.bytecode.analysis.types.JavaTypeInstance;
import org.benf.cfr.reader.entities.Method;
import org.benf.cfr.reader.state.ClassFileSourceImpl;
import org.benf.cfr.reader.state.DCCommonState;
import org.benf.cfr.reader.state.TypeUsageInformation;
import org.benf.cfr.reader.util.DecompilerCommentSource;
import org.benf.cfr.reader.util.getopt.GetOptParser;
import org.benf.cfr.reader.util.getopt.Options;
import org.benf.cfr.reader.util.getopt.OptionsImpl;
import org.benf.cfr.reader.util.output.Dumper;
import org.benf.cfr.reader.util.output.DumperFactory;
import org.benf.cfr.reader.util.output.FileSummaryDumper;
import org.benf.cfr.reader.util.output.IllegalIdentifierDump;
import org.benf.cfr.reader.util.output.NopSummaryDumper;
import org.benf.cfr.reader.util.output.StreamDumper;
import org.benf.cfr.reader.util.output.SummaryDumper;

/**
 * @author Quding Ding
 * @since 2018/1/31
 */
public class CfrPluginRunner {

  public static String compile(String[] args) {
    GetOptParser getOptParser = new GetOptParser();
    Options options = null;
    try {
      options = (Options) getOptParser.parse(args, OptionsImpl.getFactory());
    } catch (Exception e) {
      getOptParser.showHelp(OptionsImpl.getFactory(), e);
      return "params parse fail" + e.getMessage();
    }
    StringBuilder output = new StringBuilder();
    if (!options.optionIsSet(OptionsImpl.HELP) && options.getOption(OptionsImpl.FILENAME) != null) {
      ClassFileSource classFileSource = new ClassFileSourceImpl(options);
      DCCommonState dcCommonState = new DCCommonState(options, classFileSource);
      String path = (String) options.getOption(OptionsImpl.FILENAME);
      DumperFactory dumperFactory = new CfrPluginRunner.PluginDumperFactory(output);
      // 解析
      Main.doClass(dcCommonState, path, dumperFactory);
      return output.toString();
    }
    return "decompile fail";
  }

  public static class PluginDumperFactory implements DumperFactory {
    private final StringBuilder outBuffer;

    public PluginDumperFactory(StringBuilder out) {
      this.outBuffer = out;
    }

    public Dumper getNewTopLevelDumper(Options options, JavaTypeInstance classType, SummaryDumper summaryDumper, TypeUsageInformation typeUsageInformation, IllegalIdentifierDump illegalIdentifierDump) {
      return new StringStreamDumper(this.outBuffer, typeUsageInformation, options);
    }

    public SummaryDumper getSummaryDumper(Options options) {
      return (SummaryDumper)(!options.optionIsSet(OptionsImpl.OUTPUT_DIR) ? new NopSummaryDumper() : new FileSummaryDumper((String)options.getOption(OptionsImpl.OUTPUT_DIR), options, (DecompilerCommentSource)null));
    }
  }

  private static class StringStreamDumper extends StreamDumper {
    private final StringBuilder stringBuilder;

    public StringStreamDumper(StringBuilder sb, TypeUsageInformation typeUsageInformation, Options options) {
      super(typeUsageInformation, options, new IllegalIdentifierDump.Nop());
      this.stringBuilder = sb;
    }

    protected void write(String s) {
      this.stringBuilder.append(s);
    }

    public void close() {
    }

    public void addSummaryError(Method method, String s) {
    }
  }
}

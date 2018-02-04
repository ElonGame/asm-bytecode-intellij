package org.objectweb.asm.idea.util;

import org.benf.cfr.reader.Main;
import org.benf.cfr.reader.api.ClassFileSource;
import org.benf.cfr.reader.bytecode.analysis.types.JavaTypeInstance;
import org.benf.cfr.reader.entities.Method;
import org.benf.cfr.reader.state.ClassFileSourceImpl;
import org.benf.cfr.reader.state.DCCommonState;
import org.benf.cfr.reader.state.TypeUsageInformation;
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

import java.io.StringWriter;

/**
 * @author Quding Ding
 * @since 2018/1/31
 */
public class CfrPluginRunner {

  public static void compile(String[] args,StringWriter writer) {
    GetOptParser getOptParser = new GetOptParser();
    Options options = null;
    try {
      options = getOptParser.parse(args, OptionsImpl.getFactory());
    } catch (Exception e) {
      getOptParser.showHelp(OptionsImpl.getFactory(), e);
      writer.append("params parse fail").append(e.getMessage());
    }
    if (!options.optionIsSet(OptionsImpl.HELP) && options.getOption(OptionsImpl.FILENAME) != null) {
      ClassFileSource classFileSource = new ClassFileSourceImpl(options);
      DCCommonState dcCommonState = new DCCommonState(options, classFileSource);
      String path = options.getOption(OptionsImpl.FILENAME);
      DumperFactory dumperFactory = new CfrPluginRunner.PluginDumperFactory(writer);
      // 解析
      Main.doClass(dcCommonState, path, dumperFactory);
      return;
    }
    writer.append("decompile fail");
  }

  public static class PluginDumperFactory implements DumperFactory {
    private final StringWriter outBuffer;

    public PluginDumperFactory(StringWriter out) {
      this.outBuffer = out;
    }

    public Dumper getNewTopLevelDumper(Options options, JavaTypeInstance classType, SummaryDumper summaryDumper,
        TypeUsageInformation typeUsageInformation, IllegalIdentifierDump illegalIdentifierDump) {
      return new StringStreamDumper(this.outBuffer, typeUsageInformation, options);
    }

    public SummaryDumper getSummaryDumper(Options options) {
      return !options.optionIsSet(OptionsImpl.OUTPUT_DIR) ?
          new NopSummaryDumper() : new FileSummaryDumper(options.getOption(OptionsImpl.OUTPUT_DIR), options, null);
    }
  }

  private static class StringStreamDumper extends StreamDumper {
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
}

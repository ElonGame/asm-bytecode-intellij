package org.objectweb.asm.idea.cfr;

import org.benf.cfr.reader.Main;
import org.benf.cfr.reader.api.ClassFileSource;
import org.benf.cfr.reader.bytecode.analysis.types.JavaTypeInstance;
import org.benf.cfr.reader.entities.Method;
import org.benf.cfr.reader.state.ClassFileSourceImpl;
import org.benf.cfr.reader.state.DCCommonState;
import org.benf.cfr.reader.state.TypeUsageInformation;
import org.benf.cfr.reader.util.DecompilerCommentSource;
import org.benf.cfr.reader.util.Functional;
import org.benf.cfr.reader.util.MapFactory;
import org.benf.cfr.reader.util.functors.UnaryFunction;
import org.benf.cfr.reader.util.getopt.Options;
import org.benf.cfr.reader.util.getopt.OptionsImpl;
import org.benf.cfr.reader.util.output.Dumper;
import org.benf.cfr.reader.util.output.DumperFactory;
import org.benf.cfr.reader.util.output.FileSummaryDumper;
import org.benf.cfr.reader.util.output.IllegalIdentifierDump;
import org.benf.cfr.reader.util.output.NopSummaryDumper;
import org.benf.cfr.reader.util.output.StreamDumper;
import org.benf.cfr.reader.util.output.SummaryDumper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Quding Ding
 * @since 2018/1/31
 */
public class CfrPluginRunner {
  private final DCCommonState dcCommonState;
  private final IllegalIdentifierDump illegalIdentifierDump;
  private final ClassFileSource classFileSource;

  public CfrPluginRunner() {
    this(MapFactory.newMap(), (ClassFileSource)null);
  }

  public CfrPluginRunner(Map<String, String> options) {
    this(options, (ClassFileSource)null);
  }

  public CfrPluginRunner(Map<String, String> options, ClassFileSource classFileSource) {
    this.illegalIdentifierDump = new IllegalIdentifierDump.Nop();
    this.dcCommonState = initDCState(options, classFileSource);
    this.classFileSource = classFileSource;
  }

  public Options getOptions() {
    return this.dcCommonState.getOptions();
  }

  public List<List<String>> addJarPaths(String[] jarPaths) {
    List<List<String>> res = new ArrayList<>();
    String[] arr$ = jarPaths;
    int len$ = jarPaths.length;

    for(int i$ = 0; i$ < len$; ++i$) {
      String jarPath = arr$[i$];
      res.add(this.addJarPath(jarPath));
    }

    return res;
  }

  public List<String> addJarPath(String jarPath) {
    try {
      List<JavaTypeInstance> types = this.dcCommonState.explicitlyLoadJar(jarPath);
      return Functional.map(types, new UnaryFunction<JavaTypeInstance, String>() {
        public String invoke(JavaTypeInstance arg) {
          return arg.getRawName();
        }
      });
    } catch (Exception var3) {
      return new ArrayList<>(1);
    }
  }

  public String getDecompilationFor(String classFilePath) {
    try {
      StringBuilder output = new StringBuilder();
      DumperFactory dumperFactory = new CfrPluginRunner.PluginDumperFactory(output);
      Main.doClass(this.dcCommonState, classFilePath, dumperFactory);
      return output.toString();
    } catch (Exception var4) {
      return var4.toString();
    }
  }

  private static DCCommonState initDCState(Map<String, String> optionsMap, ClassFileSource classFileSource) {
    OptionsImpl options = new OptionsImpl(null, null, optionsMap);
    if (classFileSource == null) {
      classFileSource = new ClassFileSourceImpl(options);
    }
    return new DCCommonState(options, classFileSource);
  }

  private class PluginDumperFactory implements DumperFactory {
    private final StringBuilder outBuffer;

    public PluginDumperFactory(StringBuilder out) {
      this.outBuffer = out;
    }

    public Dumper getNewTopLevelDumper(Options options, JavaTypeInstance classType, SummaryDumper summaryDumper, TypeUsageInformation typeUsageInformation, IllegalIdentifierDump illegalIdentifierDump) {
      return CfrPluginRunner.this.new StringStreamDumper(this.outBuffer, typeUsageInformation, options);
    }

    public SummaryDumper getSummaryDumper(Options options) {
      return (SummaryDumper)(!options.optionIsSet(OptionsImpl.OUTPUT_DIR) ? new NopSummaryDumper() : new FileSummaryDumper((String)options.getOption(OptionsImpl.OUTPUT_DIR), options, (DecompilerCommentSource)null));
    }
  }

  class StringStreamDumper extends StreamDumper {
    private final StringBuilder stringBuilder;

    public StringStreamDumper(StringBuilder sb, TypeUsageInformation typeUsageInformation, Options options) {
      super(typeUsageInformation, options, CfrPluginRunner.this.illegalIdentifierDump);
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

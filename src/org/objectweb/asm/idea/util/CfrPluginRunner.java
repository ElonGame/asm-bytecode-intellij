package org.objectweb.asm.idea.util;

import org.benf.cfr.reader.Main;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.Pair;
import org.benf.cfr.reader.state.ClassFileSourceImpl;
import org.benf.cfr.reader.state.DCCommonState;
import org.benf.cfr.reader.util.getopt.GetOptParser;
import org.benf.cfr.reader.util.getopt.Options;
import org.benf.cfr.reader.util.getopt.OptionsImpl;
import org.benf.cfr.reader.util.output.DumperFactory;

import java.io.StringWriter;
import java.util.List;

/**
 * @author Quding Ding
 * @since 2018/1/31
 */
public class CfrPluginRunner {

  public static void compile(String[] args,StringWriter writer, byte[] classSource,String className) {
    GetOptParser getOptParser = new GetOptParser();
    Options options = null;
    List<String> files;
    try {
      Pair<List<String>, Options> processedArgs = getOptParser.parse(args, OptionsImpl.getFactory());
      files = processedArgs.getFirst();
      options = processedArgs.getSecond();
    } catch (Exception e) {
      getOptParser.showHelp(OptionsImpl.getFactory(), e);
      writer.append("params parse fail").append(e.getMessage());
      return;
    }

    if (!options.optionIsSet(OptionsImpl.HELP) && !files.isEmpty()) {
      ClassFileSourceImpl classFileSource = new ClassByteCodeSourceImpl(options,classSource,className);
      DCCommonState dcCommonState = new DCCommonState(options, classFileSource);
      String path = classFileSource.adjustInputPath(files.get(0));
      DumperFactory dumperFactory = new PluginDumperFactory(writer, options);

      // 解析
      Main.doClass(dcCommonState, path,false, dumperFactory);
      return;
    }
    writer.append("decompile fail");
  }


}

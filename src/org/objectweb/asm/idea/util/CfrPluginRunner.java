package org.objectweb.asm.idea.util;

import org.benf.cfr.reader.api.CfrDriver;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.Pair;
import org.benf.cfr.reader.util.getopt.GetOptParser;
import org.benf.cfr.reader.util.getopt.Options;
import org.benf.cfr.reader.util.getopt.OptionsImpl;

import java.io.StringWriter;
import java.util.Collections;
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
      getOptParser.showHelp(e);
      writer.append("params parse fail").append(e.getMessage());
      return;
    }

    if (!options.optionIsSet(OptionsImpl.HELP) && !files.isEmpty()) {
      CfrDriver driver = new CfrDriver.Builder()
          .withBuiltOptions(options)
          .withOutputSink(new PluginDumperFactory(writer))
          .withClassFileSource(new ClassByteCodeSourceImpl(options,classSource,className))
          .build();
      driver.analyse(Collections.singletonList(files.get(0)));
      return;
    }
    writer.append("decompile fail");
  }


}

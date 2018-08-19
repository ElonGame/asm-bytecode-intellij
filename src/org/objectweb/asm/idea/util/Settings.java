package org.objectweb.asm.idea.util;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.idea.constant.GroovyCodeStyle;

/**
 * @author Quding Ding
 * @since 2018/2/1
 */
@State(name = "ASMPluginConfiguration", storages = {@Storage("ASMPlugin.xml")})
public class Settings implements PersistentStateComponent<Settings> {

  private boolean skipFrames = false;
  private boolean skipDebug = false;
  private boolean skipCode = false;
  private boolean expandFrames = false;
  private String codeStyle = GroovyCodeStyle.LEGACY.toString();
  /**
   * 好多人不会用,因此加上默认参数
   */
  private String cfrParams = "--stringbuilder false --arrayiter fase --collectioniter false --decodelambdas false --sugarboxing false";

  /**
   * 全局单例
   */
  public static Settings getInstance() {
    return ServiceManager.getService(Settings.class);
  }

  @Nullable
  @Override
  public Settings getState() {
    return this;
  }

  @Override
  public void loadState(Settings settings) {
    XmlSerializerUtil.copyBean(settings, this);
  }


//  get set

  public boolean isSkipFrames() {
    return skipFrames;
  }

  public void setSkipFrames(boolean skipFrames) {
    this.skipFrames = skipFrames;
  }

  public boolean isSkipDebug() {
    return skipDebug;
  }

  public void setSkipDebug(boolean skipDebug) {
    this.skipDebug = skipDebug;
  }

  public boolean isSkipCode() {
    return skipCode;
  }

  public void setSkipCode(boolean skipCode) {
    this.skipCode = skipCode;
  }

  public boolean isExpandFrames() {
    return expandFrames;
  }

  public void setExpandFrames(boolean expandFrames) {
    this.expandFrames = expandFrames;
  }

  public String getCodeStyle() {
    return codeStyle;
  }

  public void setCodeStyle(String codeStyle) {
    this.codeStyle = codeStyle;
  }

  public String getCfrParams() {
    return cfrParams;
  }

  public void setCfrParams(String cfrParams) {
    if (null == cfrParams) {
      this.cfrParams = "";
    } else {
      this.cfrParams = cfrParams.trim();
    }
  }
}

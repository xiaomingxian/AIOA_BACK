package com.cfcc.config.activiti;

import com.cfcc.common.spring.SpringContextUtilsAIOA;
import org.activiti.bpmn.model.AssociationDirection;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.image.exception.ActivitiImageException;
import org.activiti.image.impl.DefaultProcessDiagramCanvas;
import org.activiti.image.util.ReflectUtil;
import org.springframework.context.ApplicationContext;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;


public class CustomProcessDiagramCanvas extends DefaultProcessDiagramCanvas {


    protected static Color LABEL_COLOR = new Color(0, 0, 0);

    //font
    protected String activityFontName = "宋体";
    protected String labelFontName = "宋体";
    protected String annotationFontName = "宋体";

    private static volatile boolean flag = false;

    public CustomProcessDiagramCanvas(int width, int height, int minX, int minY, String imageType) {
        super(width, height, minX, minY, imageType);
    }

    public CustomProcessDiagramCanvas(int width, int height, int minX, int minY, String imageType,
                                      String activityFontName, String labelFontName, String annotationFontName, ClassLoader customClassLoader) {
        super(width, height, minX, minY, imageType, activityFontName, labelFontName, annotationFontName,
                customClassLoader);
    }

    public void drawHighLight(boolean isStartOrEnd, int x, int y, int width, int height, Color color) {
        Paint originalPaint = g.getPaint();
        Stroke originalStroke = g.getStroke();

        g.setPaint(color);
        g.setStroke(MULTI_INSTANCE_STROKE);
        if (isStartOrEnd) {// 开始、结束节点画圆
            g.drawOval(x, y, width, height);
        } else {// 非开始、结束节点画圆角矩形
            RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, width, height, 5, 5);
            g.draw(rect);
        }
        g.setPaint(originalPaint);
        g.setStroke(originalStroke);
    }

    public void drawSequenceflow(int[] xPoints, int[] yPoints, boolean conditional, boolean isDefault,
                                 boolean highLighted, double scaleFactor, Color color) {
        drawConnection(xPoints, yPoints, conditional, isDefault, "sequenceFlow", AssociationDirection.ONE, highLighted,
                scaleFactor, color);
    }

    public void drawConnection(int[] xPoints, int[] yPoints, boolean conditional, boolean isDefault,
                               String connectionType, AssociationDirection associationDirection, boolean highLighted, double scaleFactor,
                               Color color) {

        Paint originalPaint = g.getPaint();
        Stroke originalStroke = g.getStroke();

        g.setPaint(CONNECTION_COLOR);
        if (connectionType.equals("association")) {
            g.setStroke(ASSOCIATION_STROKE);
        } else if (highLighted) {
            g.setPaint(color);
            g.setStroke(HIGHLIGHT_FLOW_STROKE);
        }

        for (int i = 1; i < xPoints.length; i++) {
            Integer sourceX = xPoints[i - 1];
            Integer sourceY = yPoints[i - 1];
            Integer targetX = xPoints[i];
            Integer targetY = yPoints[i];
            Line2D.Double line = new Line2D.Double(sourceX, sourceY, targetX, targetY);
            g.draw(line);
        }

        if (isDefault) {
            Line2D.Double line = new Line2D.Double(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
            drawDefaultSequenceFlowIndicator(line, scaleFactor);
        }

        if (conditional) {
            Line2D.Double line = new Line2D.Double(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
            drawConditionalSequenceFlowIndicator(line, scaleFactor);
        }

        if (associationDirection.equals(AssociationDirection.ONE)
                || associationDirection.equals(AssociationDirection.BOTH)) {
            Line2D.Double line = new Line2D.Double(xPoints[xPoints.length - 2], yPoints[xPoints.length - 2],
                    xPoints[xPoints.length - 1], yPoints[xPoints.length - 1]);
            drawArrowHead(line, scaleFactor);
        }
        if (associationDirection.equals(AssociationDirection.BOTH)) {
            Line2D.Double line = new Line2D.Double(xPoints[1], yPoints[1], xPoints[0], yPoints[0]);
            drawArrowHead(line, scaleFactor);
        }
        g.setPaint(originalPaint);
        g.setStroke(originalStroke);
    }

    public void drawLabel(boolean highLighted, String text, GraphicInfo graphicInfo, boolean centered) {
        float interline = 1.0f;

        // text
        if (text != null && text.length() > 0) {
            Paint originalPaint = g.getPaint();
            Font originalFont = g.getFont();
            if (highLighted) {
                g.setPaint(WorkflowConstants.COLOR_NORMAL);
            } else {
                g.setPaint(LABEL_COLOR);
            }
            g.setFont(new Font(labelFontName, Font.BOLD, 10));

            int wrapWidth = 100;
            int textY = (int) graphicInfo.getY();

            // TODO: use drawMultilineText()
            AttributedString as = new AttributedString(text);
            as.addAttribute(TextAttribute.FOREGROUND, g.getPaint());
            as.addAttribute(TextAttribute.FONT, g.getFont());
            AttributedCharacterIterator aci = as.getIterator();
            FontRenderContext frc = new FontRenderContext(null, true, false);
            LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);

            while (lbm.getPosition() < text.length()) {
                TextLayout tl = lbm.nextLayout(wrapWidth);
                textY += tl.getAscent();
                Rectangle2D bb = tl.getBounds();
                double tX = graphicInfo.getX();
                if (centered) {
                    tX += (int) (graphicInfo.getWidth() / 2 - bb.getWidth() / 2);
                }
                tl.draw(g, (float) tX, textY);
                textY += tl.getDescent() + tl.getLeading() + (interline - 1.0f) * tl.getAscent();
            }

            // restore originals
            g.setFont(originalFont);
            g.setPaint(originalPaint);
        }
    }

    @Override
    public BufferedImage generateBufferedImage(String imageType) {
        if (closed) {
            throw new ActivitiImageException("ProcessDiagramGenerator already closed");
        }

        // Try to remove white space
        minX = (minX <= WorkflowConstants.PROCESS_PADDING) ? WorkflowConstants.PROCESS_PADDING : minX;
        minY = (minY <= WorkflowConstants.PROCESS_PADDING) ? WorkflowConstants.PROCESS_PADDING : minY;
        BufferedImage imageToSerialize = processDiagram;
        if (minX >= 0 && minY >= 0) {
            imageToSerialize = processDiagram.getSubimage(
                    minX - WorkflowConstants.PROCESS_PADDING,
                    minY - WorkflowConstants.PROCESS_PADDING,
                    canvasWidth - minX + WorkflowConstants.PROCESS_PADDING,
                    canvasHeight - minY + WorkflowConstants.PROCESS_PADDING);
        }
        return imageToSerialize;
    }

    @Override
    public void initialize(String imageType) {
        this.processDiagram = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
        this.g = processDiagram.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(Color.black);

        Font font = this.loadFontFromResources(this.activityFontName, Font.BOLD, FONT_SIZE);
        g.setFont(font);
        this.fontMetrics = g.getFontMetrics();

        LABEL_FONT = this.loadFontFromResources(this.labelFontName, Font.ITALIC, 10);
        ANNOTATION_FONT = this.loadFontFromResources(this.annotationFontName, Font.PLAIN, FONT_SIZE);
        //优化加载速度
        if (flag) {
            return;
        }
        try {
            USERTASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/userTask.png", customClassLoader));
            SCRIPTTASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/scriptTask.png", customClassLoader));
            SERVICETASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/serviceTask.png", customClassLoader));
            RECEIVETASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/receiveTask.png", customClassLoader));
            SENDTASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/sendTask.png", customClassLoader));
            MANUALTASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/manualTask.png", customClassLoader));
            BUSINESS_RULE_TASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/businessRuleTask.png", customClassLoader));
            SHELL_TASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/shellTask.png", customClassLoader));
            CAMEL_TASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/camelTask.png", customClassLoader));
            MULE_TASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/muleTask.png", customClassLoader));

            TIMER_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/timer.png", customClassLoader));
            COMPENSATE_THROW_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/compensate-throw.png", customClassLoader));
            COMPENSATE_CATCH_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/compensate.png", customClassLoader));
            ERROR_THROW_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/error-throw.png", customClassLoader));
            ERROR_CATCH_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/error.png", customClassLoader));
            MESSAGE_THROW_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/message-throw.png", customClassLoader));
            MESSAGE_CATCH_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/message.png", customClassLoader));
            SIGNAL_THROW_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/signal-throw.png", customClassLoader));
            SIGNAL_CATCH_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/signal.png", customClassLoader));
/*        String baseUrl = Thread.currentThread().getContextClassLoader().getResource("static/img/activiti/").getPath();
          SCRIPTTASK_IMAGE = ImageIO.read(new FileInputStream(baseUrl+"scriptTask.png"));
          USERTASK_IMAGE = ImageIO.read(new FileInputStream(baseUrl+"userTask.png"));
          SERVICETASK_IMAGE = ImageIO.read(new FileInputStream(baseUrl+"serviceTask.png"));
          RECEIVETASK_IMAGE = ImageIO.read(new FileInputStream(baseUrl+"receiveTask.png"));
          SENDTASK_IMAGE = ImageIO.read(new FileInputStream(baseUrl+"sendTask.png"));
          MANUALTASK_IMAGE = ImageIO.read(new FileInputStream(baseUrl+"manualTask.png"));
          BUSINESS_RULE_TASK_IMAGE = ImageIO.read(new FileInputStream(baseUrl+"businessRuleTask.png"));
          SHELL_TASK_IMAGE = ImageIO.read(new FileInputStream(baseUrl+"shellTask.png"));
          CAMEL_TASK_IMAGE = ImageIO.read(new FileInputStream(baseUrl+"camelTask.png"));
          MULE_TASK_IMAGE = ImageIO.read(new FileInputStream(baseUrl+"muleTask.png"));

          TIMER_IMAGE = ImageIO.read(new FileInputStream(baseUrl+"timer.png"));
          COMPENSATE_THROW_IMAGE = ImageIO.read(new FileInputStream(baseUrl+"compensate-throw.png"));
          COMPENSATE_CATCH_IMAGE = ImageIO.read(new FileInputStream(baseUrl+"compensate.png"));
          ERROR_THROW_IMAGE = ImageIO.read(new FileInputStream(baseUrl+"error-throw.png"));
          ERROR_CATCH_IMAGE = ImageIO.read(new FileInputStream(baseUrl+"error.png"));
          MESSAGE_THROW_IMAGE = ImageIO.read(new FileInputStream(baseUrl+"message-throw.png"));
          MESSAGE_CATCH_IMAGE = ImageIO.read(new FileInputStream(baseUrl+"message.png"));
          SIGNAL_THROW_IMAGE = ImageIO.read(new FileInputStream(baseUrl+"signal-throw.png"));
          SIGNAL_CATCH_IMAGE = ImageIO.read(new FileInputStream(baseUrl+"signal.png"));*/
            flag = true;
        } catch (IOException e) {
            flag = false;
            LOGGER.warn("Could not load image for process diagram creation: {}", e.getMessage());
        }
    }


    /**
     * 根据名称从resources中加载字体
     *
     * @param fontName
     * @param fontStyle
     * @param fontSize
     * @return
     */
    public Font loadFontFromResources(String fontName, int fontStyle, float fontSize) {
        Font font = null;
//        System.setProperty("java.awt.headless", "true");
//        if (StringUtils.isEmpty(fontName)) {
//            return font;
//        }
        try {

            ApplicationContext applicationContext = SpringContextUtilsAIOA.applicationContextGet();
            if (applicationContext==null){
                System.err.println("===============>>>>>spring容器为空  流程图生成Canvas 初始化的时候为空正常");
                return null;//
            }
            FontBean bean = applicationContext.getBean(FontBean.class);
            String fontPath = bean.getFontPath();
            if (fontPath == null) {
                System.err.println("---------->>>配置文件路径为空");
                return null;
            }
            //加载resources下的字体文件,文件命名与设置的字体名称一一对应
            InputStream fontInputStream = new FileInputStream(new File(fontPath));
//            System.err.println("=====>>>读取到的字体流：："+fontPath+","+fontInputStream);
            //未找到项目中相应的字体文件，则使用系统的字体
            if (fontInputStream == null) {
                Font font1 = new Font(fontName, fontStyle, (int) fontSize);
                System.out.println("------字体流为null,new出的字体：：" + font1);
                return font1;
            }
            font = Font.createFont(Font.TRUETYPE_FONT, fontInputStream);
//            System.err.println("------->>>生成的字体：："+font);

            //设置字体类型
            if (fontStyle > -1) {
                font = font.deriveFont(fontStyle);
            }
            //设置字体大小
            if (fontSize > -1) {
                font = font.deriveFont(fontSize);
            }
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return font;
    }


}
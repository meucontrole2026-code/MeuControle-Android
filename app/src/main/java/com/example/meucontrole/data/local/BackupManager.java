package com.example.meucontrole.data.local;

import android.content.Context;
import android.content.Intent;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BackupManager {

    public static void exportarBancoDeDados(Context context) {
        try {
            File dbFile = context.getDatabasePath("controle_financeiro_db");
            if (!dbFile.exists()) return;

            File backupFile = new File(context.getExternalFilesDir(null), "backup_financeiro.db");

            try (InputStream input = new FileInputStream(dbFile);
                 OutputStream output = new FileOutputStream(backupFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = input.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            android.net.Uri uri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".fileprovider",
                    backupFile
            );

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/octet-stream");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(Intent.createChooser(intent, "Exportar Cópia de Segurança (.db)"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package de.elatexam.editor.pages;

import java.io.IOException;
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.databinder.hib.Databinder;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.lang.Bytes;
import org.hibernate.Session;

import com.visural.wicket.component.submitters.IndicateModalSubmitLink;

import de.elatexam.editor.TaskEditorSession;
import de.elatexam.editor.user.BasicUser;
import de.elatexam.editor.util.Stuff;
import de.elatexam.model.ComplexTaskDef;
import de.elatexam.model.SubTaskDef;
import de.thorstenberger.taskmodel.util.JAXBUtils;

/**
 * @author sdienst
 */
public class UploadComplexTaskdefPage extends SecurePage {

  private class FileUploadForm<T> extends Form<T> {
    private FileUploadField fileUploadField;

    public FileUploadForm(final String name) {
      super(name);

      // set this form to multipart mode (allways needed for uploads!)
      setMultiPart(true);

      // Add one file input field
      add(fileUploadField = new FileUploadField("fileInput"));

      setMaxSize(Bytes.kilobytes(1000));
      add(new FeedbackPanel("feedback"));
      add(new IndicateModalSubmitLink("submit"));
    }

    /**
     * @see org.apache.wicket.markup.html.form.Form#onSubmit()
     */
    @Override
    protected void onSubmit() {
      final FileUpload upload = fileUploadField.getFileUpload();
      if (upload != null) {
        try {
          final ComplexTaskDef taskdef = loadTaskDef(upload);
          if (taskdef != null) {
            persistIntoDB(taskdef);
            setResponsePage(StatisticPage.class);
          } else {
            error(String.format("Die Datei '%s' enthält keine gültige Aufgabendefinition!", upload.getClientFileName()));
          }
        } catch (final Exception e) {
          throw new IllegalStateException("Unable to add new complextaskdef to database!", e);
        }
      }
    }
  }

  public UploadComplexTaskdefPage() {
    add(new FileUploadForm("uploadform"));
  }


  /**
   * @param upload
   * @return
   */
  public ComplexTaskDef loadTaskDef(final FileUpload upload) {
	final JAXBContext context = Stuff.getContext(); 
	Unmarshaller unmarshaller = null;
	try {
      unmarshaller = JAXBUtils.getJAXBUnmarshaller(context);

      final Object result = unmarshaller.unmarshal(upload.getInputStream());
      return (ComplexTaskDef) result;

    } catch (final JAXBException e) {
      e.printStackTrace();
    } catch (final IOException e) {
      e.printStackTrace();
    }finally{
      if(unmarshaller!=null)
    	JAXBUtils.releaseJAXBUnmarshaller(context, unmarshaller);
    }
    return null;
  }

  public void persistIntoDB(final ComplexTaskDef taskdef) throws Exception {
    final Session session = Databinder.getHibernateSession();

        Collection<SubTaskDef> newSubtaskdefs = Stuff.getAllSubtaskdefs(taskdef);
        for (SubTaskDef std : newSubtaskdefs) {
            session.save(std);
        }
        session.save(taskdef);

    // add to current user
    final BasicUser user = TaskEditorSession.get().getUser();
    user.getTaskdefs().add(taskdef);
    user.getSubtaskdefs().addAll(newSubtaskdefs);

    session.saveOrUpdate(user);
    session.getTransaction().commit();

  }
}

/*

Copyright (C) 2009 Steffen Dienst

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package de.elatexam.editor.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import net.databinder.hib.Databinder;

import org.hibernate.Transaction;
import org.hibernate.Session;

import com.google.common.collect.ImmutableMap;

import de.elatexam.model.AddonSubTaskDef;
import de.elatexam.model.Category;
import de.elatexam.model.ClozeSubTaskDef;
import de.elatexam.model.ClozeTaskBlock;
import de.elatexam.model.ComplexTaskDef;
import de.elatexam.model.MappingSubTaskDef;
import de.elatexam.model.MappingTaskBlock;
import de.elatexam.model.McSubTaskDef;
import de.elatexam.model.McTaskBlock;
import de.elatexam.model.PaintSubTaskDef;
import de.elatexam.model.PaintTaskBlock;
import de.elatexam.model.SubTaskDef;
import de.elatexam.model.TaskBlock;
import de.elatexam.model.TextSubTaskDef;
import de.elatexam.model.TextTaskBlock;
import de.elatexam.model.manual.HomogeneousTaskBlock;
import de.thorstenberger.taskmodel.complex.jaxb.ComplexTaskDef.Category.AddonTaskBlock;
import de.thorstenberger.taskmodel.complex.jaxb.TaskBlockType;

/**
 * Misc. helper functions.
 *
 * @author Steffen Dienst
 *
 */
public class Stuff {
	static JAXBContext context;
	static{
		try {
			context=JAXBContext.newInstance(ComplexTaskDef.class);
		} catch (JAXBException e) {
			e.printStackTrace();
			context = null;
		}
	}
	public static JAXBContext getContext(){
		return context;
	}
  /**
   * Get the items (subtaskdef|choice(subtaskdef)+)* of a taskblock. Needs to be done via reflection, because the items
   * have no common interface but similar method names. This method assumes that each subclass of {@link TaskBlockType}
   * has exactly one method named "get...SubTaskDefOrChoiceItems" that gets invoked.
   *
   * @param tb
   *          taskblock
   * @return list of items or empty list.
   */
    public static List<SubTaskDef> getSubtaskDefs(final HomogeneousTaskBlock tb) {
    	return (List<SubTaskDef>) tb.getSubtaskDefs();
  }

    public static <T extends SubTaskDef> Collection<T> getAllSubtaskdefs(final ComplexTaskDef taskdef) {
    final Collection<T> stds = new ArrayList<T>();
    for (final Category cat : taskdef.getCategory()) {
            for (final TaskBlock block : cat.getTaskBlocks()) {
                stds.addAll((Collection<? extends T>) getSubtaskDefs((HomogeneousTaskBlock) block));
      }
    }
    return stds;
  }
    /**
     * Make all xmlids of every subtaskdef unique by prepending an increasing number to it.
     * @param taskdef
     */
    public static void makeIDsUnique(ComplexTaskDef taskdef){
    	int i=0;
    	for(SubTaskDef std: getAllSubtaskdefs(taskdef)){
    		std.setXmlid(i++ + std.getXmlid());
    	}
    }

    /**
     * TODO create DAO layer!
     *
     * @param objects
     */
    public static void saveAll(Object... objects) {
        final Session session = Databinder.getHibernateSession();
        for (Object obj : objects) {
          if (obj != null && !session.contains(obj)) {
            session.saveOrUpdate(obj);
          }
        }
        session.getTransaction().commit();
    }

    public static <T> T newInstance(Class<T> clazz){
      try {
        return clazz.newInstance();
      } catch (InstantiationException e) {
        e.printStackTrace();
        return null;
      } catch (IllegalAccessException e) {
        e.printStackTrace();
        return null;
      }
    }
}

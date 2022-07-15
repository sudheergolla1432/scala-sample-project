package service

import dao.UniversityCourseDao
import entities.Student

class UniversityCourseService {

  def getAllStudents() : Option[List[Student]] ={
      UniversityCourseDao.findAllStudents
  }
}

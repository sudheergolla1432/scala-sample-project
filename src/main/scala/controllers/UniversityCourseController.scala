package controllers

import cats._
import cats.effect._
import dao.UniversityCourseDao
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._
import org.http4s.dsl.impl._
import org.http4s.implicits._
import org.http4s.server._
import org.http4s.server.blaze.BlazeServerBuilder

object UniversityCourseController extends IOApp {

  /**
   *  The framework used here is scala http4s
   */

  /**
   * I should display all courses // get api which gives me course id

      I should allow student to register for particular course // based on course id from above should allow student to register for a course

  api's:

      display all students -> GET  api/student/all
      display all courses  -> GET  api/course/all
      register for course  -> POST api/register?studentid=1&courseid=2
      add a student        -> POST api/student?studentName=""
      add a course         -> POST api/course
      display all students
         and their courses -> GET  api/studentDetails
   */
  //TODO:
  //Get /student?studentId=1&courseIdd=1
  def universityRoutes[F[_] :Monad] : HttpRoutes[F] ={
    val dsl = Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F]{

      case GET -> Root / "student" / "all" =>
        val studentDetails = UniversityCourseDao.findAllStudents
        Ok(studentDetails.asJson)

      case GET -> Root / "student" / "all" / "v2" =>
        UniversityCourseDao.findAllStudents match {
          case Some(students) => Ok(students.asJson)
          case _ => NotFound("Nothing found")
        }

      case POST -> Root / "student" :? StudentNameQueryParamMatcher(studentName) =>
        Created(UniversityCourseDao.saveStudent(studentName).asJson)

      case GET -> Root / "course" / "all" =>
        val courseDetails = UniversityCourseDao.findAllCourses
        Ok(courseDetails.asJson)


      case POST -> Root / "course" :? CourseNameQueryParamMatcher(courseName) =>
        Created(UniversityCourseDao.saveCourse(courseName).asJson)

      case POST -> Root / "register" :? StudentIdQueryParamMatcher(studentId) +& CourseIdQueryParamMatcher(courseId)   =>
        UniversityCourseDao.registerForCourse(studentId.toInt,courseId.toInt) match {
          case Some(x) => Ok(x.asJson)
          case _ => BadRequest("Bad Request")
        }

      case GET -> Root / "studentcourse" / "all" =>
        val studentDetails = UniversityCourseDao.findAllStudentsAndCourse
        Ok(studentDetails.asJson)
    }


  }

  object StudentIdQueryParamMatcher extends QueryParamDecoderMatcher[String]("studentId")
  object CourseIdQueryParamMatcher extends QueryParamDecoderMatcher[String]("courseId")
  object StudentNameQueryParamMatcher extends QueryParamDecoderMatcher[String]("studentName")
  object CourseNameQueryParamMatcher extends QueryParamDecoderMatcher[String]("courseName")

 /* def allRoutes[F[_] : Monad] : HttpRoutes[F] =
    universityRoutes[F]

  def allRoutesComplete[F[_]:Monad] : HttpApp[F] =
    allRoutes[F].orNotFound
*/
  override def run(args: List[String]): IO[ExitCode] = {
    val apis = Router(
      "/api" -> universityRoutes[IO]
    ).orNotFound

    BlazeServerBuilder[IO](runtime.compute)
      .bindHttp(8091,"localhost")
      .withHttpApp(apis)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)

  }
}

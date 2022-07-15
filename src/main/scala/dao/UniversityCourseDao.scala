package dao

/**
 * framework used is Doobie with postgres
 */

import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO, IOApp}
import doobie.{HC, HPS}
import doobie.implicits._
import doobie.util.transactor.Transactor
import entities.{Course, Student, StudentCourse};

object UniversityCourseDao extends IOApp{

  implicit class Debugger[A](io:IO[A]){
    def debug: IO[A] = io.map{
      a =>
        println(s" $a ")
        a
    }
  }

  //Transactor is a data type that will allow us to run a connection to database run a query and return some values

  //TODO: Read more about cats effect
  //TODO : Explore transactions (partially done)
  //This transactor handles transactions for us it will commit changes , but regarding rollback we
  //we have some configurations
  val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:university",
    "docker",
    "docker"
  )

  //find all students
  def findAllStudents: Option[List[Student]] ={
    val query = sql"select id,name from student".query[Student]
    val action = query.to[List]
    Option(action.transact(xa).unsafeRunSync())
  }

  //find all courses
  def findAllCourses: List[Course] ={
    val query = sql"select id,name from courses".query[Course]
    val action = query.to[List]
    action.transact(xa).unsafeRunSync()
  }

  //save student
  //TODO : will this query prone to sql injection (Done)
  //Don’t worry, this code is safely protected from SQL injection attacks. ${name} will be a place holder.

  //TODO: Write an example using HC (Done)
  //HC -> high level connection
  //HPC -> high level prepared statement
  def findStudentByNameUsingHCProgram(studentName: String): IO[Option[Student]] = {
    val query = "select id, name from student where name = ?"
    HC.stream[Student](
      query,
      HPS.set(studentName),   // Parameters start from index 1 by default
      512
    ).compile
      .toList
      .map(_.headOption)
      .transact(xa)
  }

  //TODO: Connection pooling (Done)
  //connection pool is done by using doobie hikari dependency.

  //TODO : Try using threads
  def saveStudent(name: String): Int = {
    sql"insert into Student (name) values ($name)"
      .update.withUniqueGeneratedKeys[Int]("id")
      .transact(xa).unsafeRunSync()
  }

  //save course
  def saveCourse(name: String): Int = {
    sql"insert into Courses (name) values ($name)"
      .update.withUniqueGeneratedKeys[Int]("id")
      .transact(xa).unsafeRunSync()
  }

  //display all students and their courses based on course name
  def findStudentByCourseTitle(title:String):List[StudentCourse] ={
    val statement =
      sql"""
        select s.id, s.name , c.name from student s join student_course sc ON s.id = sc.student_id
        join courses c ON c.id = sc.course_id
        where c.name = $title
      """
    statement.query[StudentCourse].to[List].transact(xa).unsafeRunSync()
  }

  //display all students and their
  def findAllStudentsAndCourse():List[StudentCourse] ={
    val statement =
      sql"""
        select s.id, s.name , c.name from student s join student_course sc ON s.id = sc.student_id
        join courses c ON c.id = sc.course_id
      """
    statement.query[StudentCourse].to[List].transact(xa).unsafeRunSync()
  }

  //register for a course
  def registerForCourse(studentId: Int,courseId:Int): Option[Int] = {
    Option(sql"insert into student_course (student_id,course_id) values ($studentId , $courseId)"
      .update.run
      .transact(xa).unsafeRunSync())
  }

  override def run(args: List[String]): IO[ExitCode] = {
    //findAllStudentNamesWitCustomClass.map(println).as(ExitCode.Success);
    //saveStudentWithAutoGenId("Jimmy").debug.as(ExitCode.Success)
    //findStudentById(1).debug.as(ExitCode.Success)
    //saveStudent("Modi").as(ExitCode.Success)
    //findStudentByCourseTitle("maths").map(println).as(ExitCode.Success)
   // findAllStudentsAndCourse.map(student => student.map(x => println(x))).as(ExitCode.Success)


    //findAllCourses.map(println).as(ExitCode.Success)
    //registerForCourse(4,1).debug.as(ExitCode.Success)
    findStudentById(1).debug.as(ExitCode.Success)
  }





  def findStudentById(id:Int): IO[Option[Student]] ={
    val query = sql"select id,name from student where id = $id".query[Student]
    val action = query.option
    action.transact(xa)
  }

  /*val studentNamesStreams = sql"select name from Student".query[String]
    .stream.compile.toList.transact(xa)*/

  /*def saveStudent(id: Int , name: String): IO[Int] = {
    val query = sql"insert into Student (id, name) values ($id, $name)"
    query.update.run.transact(xa)
  }*/

  /* //type classes
   class StudentName(val value:String){
     override def toString: String = value
   }

   def findAllStudentNamesWitCustomClass:IO[List[StudentName]] =
     sql"select name from Student".query[StudentName].to[List].transact(xa)

   object StudentName {

     implicit  val studentNameGet: Get[StudentName] = Get[String].map(string => new StudentName(string))
     implicit val studentNamePut: Put[StudentName] = Put[String].contramap(sname => sname.value)

   }*/
}

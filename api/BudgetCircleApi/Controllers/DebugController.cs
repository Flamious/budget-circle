//namespace BudgetCircleApi.Controllers
//{
//    using BudgetCircleApi.DAL.Interfaces;
//    using Microsoft.AspNetCore.Mvc;
//    using System.Collections.Generic;

//    [ApiController]
//    [Route("[controller]")]
//    public class DebugController : ControllerBase
//    {

//        private readonly IDbRepository _repository;

//        public DebugController(IDbRepository repository)
//        {
//            _repository = repository;
//        }

//        [HttpGet]
//        [Route("exp")]
//        public IEnumerable<DAL.Entities.ExpenseType> GetExp()
//        {
//            return _repository.ExpenseTypes.GetAll();
//        }

//        [HttpGet]
//        [Route("ear")]
//        public IEnumerable<DAL.Entities.EarningType> GetEar()
//        {
//            return _repository.EarningTypes.GetAll();
//        }

//        [HttpGet]
//        [Route("bt")]
//        public IEnumerable<DAL.Entities.BudgetType> GetBt()
//        {
//            return _repository.BudgetTypes.GetAll();
//        }

//        [HttpGet]
//        [Route("op")]
//        public IEnumerable<DAL.Entities.Operation> GetOp()
//        {
//            return _repository.Operations.GetAll();
//        }

//        [HttpGet]
//        [Route("us")]
//        public IEnumerable<DAL.Entities.User> GetUs()
//        {
//            return _repository.Users.GetAll();
//        }
//    }
//}




//namespace BudgetCircleApi.Controllers
//{
//    using BudgetCircleApi.BLL.Interfaces;
//    using Microsoft.AspNetCore.Mvc;
//    using System;
//    using System.Threading.Tasks;

//    [ApiController]
//    [Route("[controller]")]
//    //[Authorize(AuthenticationSchemes = "Bearer", Roles = UserRoles.Admin)]
//    public class AdminController : Controller
//    {
//        private readonly IAdminServices _adminServices;

//        public AdminController(IAdminServices adminServices)
//        {
//            _adminServices = adminServices;
//        }

//        [HttpGet]
//        [Route("initRoles")]
//        public async Task<IActionResult> InitializeRoles()
//        {
//            try
//            {
//                await _adminServices.InitializeRoles();
//                return Ok();
//            }
//            catch (Exception e)
//            {
//                return BadRequest(e.Message);
//            }
//        }

//        [HttpGet]
//        [Route("initTypes")]
//        public async Task<IActionResult> InitializeTypes()
//        {
//            try
//            {
//                await _adminServices.InitializeTypes();
//                return Ok();
//            }
//            catch (Exception e)
//            {
//                return BadRequest(e.Message);
//            }
//        }
//    }
//}

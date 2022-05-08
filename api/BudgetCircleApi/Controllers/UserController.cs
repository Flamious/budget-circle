namespace BudgetCircleApi.Controllers
{
    using BudgetCircleApi.BLL.Models.Request;
    using BudgetCircleApi.BLL.Models.Response;
    using Microsoft.AspNetCore.Authorization;
    using Microsoft.AspNetCore.Mvc;
    using System;
    using System.Linq;
    using System.Security.Claims;
    using System.Threading.Tasks;

    [ApiController]
    [Route("[controller]")]
    public class UserController : Controller
    {
        private readonly BLL.Interfaces.IAuthorizationService _authorizationService;

        public UserController(BLL.Interfaces.IAuthorizationService authorizationService)
        {
            _authorizationService = authorizationService;
        }

        [HttpPost]
        [Route("token")]
        [Authorize(AuthenticationSchemes = "Bearer")]
        public async Task<IActionResult> RefreshToken()
        {
            try
            {
                string userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                var response = await _authorizationService.RefreshToken(userId);

                if (response is AuthorizationResponse)
                {
                    return Ok(response);
                }

                return BadRequest(response);
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpPost]
        [Route("newpassword")]
        [Authorize(AuthenticationSchemes ="Bearer")]
        public async Task<IActionResult> ChangePassword([FromForm] NewPasswordModel model)
        {
            if (!ModelState.IsValid)
            {
                var errors = ModelState.Values.SelectMany(e => e.Errors.Select(er => er.ErrorMessage));
                string errorList = "";
                foreach(var error in errors)
                {
                    errorList += $"{error}\n";
                }

                return BadRequest(new ErrorResponse(errorList));
            }

            try
            {
                string userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                var response = await _authorizationService.ChangePassword(userId, model);

                if (response is ErrorResponse)
                {
                    return BadRequest(response);
                }

                return Ok(response);
            }
            catch (Exception e)
            {
                return BadRequest(new ErrorResponse(e.Message));
            }
        }

        [HttpPost]
        [Route("signin")]
        public async Task<IActionResult> SignIn([FromForm] SignInModel model)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState.Values.SelectMany(e => e.Errors.Select(er => er.ErrorMessage)));
            }

            try
            {
                var response = await _authorizationService.SignIn(model);

                if (response is AuthorizationResponse)
                {
                    return Ok(response);
                }

                return BadRequest(response);
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpPost]
        [Route("signup")]
        public async Task<IActionResult> SignUp([FromForm] SignUpModel model)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState.Values.SelectMany(e => e.Errors.Select(er => er.ErrorMessage)));
            }

            try
            {
                var response = await _authorizationService.SignUp(model);

                if (response is AuthorizationResponse)
                {
                    return Ok(response);
                }

                return BadRequest(response);
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }
    }
}

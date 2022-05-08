namespace BudgetCircleApi.Controllers
{
    using BudgetCircleApi.BLL.Interfaces;
    using BudgetCircleApi.BLL.Models.Request;
    using BudgetCircleApi.BLL.Models.Response;
    using Microsoft.AspNetCore.Authorization;
    using Microsoft.AspNetCore.Mvc;
    using System;
    using System.Security.Claims;
    using System.Threading.Tasks;

    [ApiController]
    [Route("[controller]")]
    public class BudgetTypeController : Controller
    {
        private readonly IBudgetTypesService _budgetTypesServices;

        public BudgetTypeController(IBudgetTypesService budgetTypesServices)
        {
            _budgetTypesServices = budgetTypesServices;
        }

        [HttpGet]
        [Authorize(AuthenticationSchemes = "Bearer")]
        public async Task<IActionResult> GetAllBudgetTypes()
        {
            try
            {
                string userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                var result = await Task.Run(() => _budgetTypesServices.GetBudgetTypes(userId));

                return Ok(result);
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpPut]
        [Authorize(AuthenticationSchemes = "Bearer")]
        public async Task<IActionResult> AddBudgetType([FromQuery] BudgetTypeModel model)
        {
            try
            {
                string userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                var response = await _budgetTypesServices.AddBudgetType(userId, model.Title, model.Sum);

                if (response is ErrorResponse)
                {
                    return BadRequest(response);
                }

                return Ok(response);
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpDelete]
        [Route("{id:int}")]
        [Authorize(AuthenticationSchemes = "Bearer")]
        public async Task<IActionResult> RemoveBudgetType([FromRoute] int id)
        {
            try
            {
                string userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                await _budgetTypesServices.RemoveBudgetType(userId, id);

                return NoContent();
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpDelete]
        [Route("clearsums")]
        [Authorize(AuthenticationSchemes = "Bearer")]
        public async Task<IActionResult> ClearBudgetTypeSums()
        {
            try
            {
                string userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                await _budgetTypesServices.ClearBudgetTypeSums(userId);

                return NoContent();
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpPost]
        [Route("{id:int}")]
        [Authorize(AuthenticationSchemes = "Bearer")]
        public async Task<IActionResult> UpdateBudgetType([FromRoute] int id, [FromQuery] BudgetTypeModel model)
        {
            try
            {
                string userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                await _budgetTypesServices.UpdateBudgetType(userId, id, model.Title, model.Sum);

                return Ok();
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }
    }
}
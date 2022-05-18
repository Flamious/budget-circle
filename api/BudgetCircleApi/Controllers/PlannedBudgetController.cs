namespace BudgetCircleApi.Controllers
{
    using BudgetCircleApi.BLL.Interfaces;
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
    public class PlannedBudgetController : Controller
    {
        private readonly IPlannedBudgetService _plannedBudgetService;

        public PlannedBudgetController(IPlannedBudgetService plannedBudgetService)
        {
            _plannedBudgetService = plannedBudgetService;
        }

        [HttpGet]
        [Authorize(AuthenticationSchemes = "Bearer")]
        public async Task<IActionResult> GetPlannedBudget([FromQuery] PlannedBudgetRequest request)
        {
            try
            {
                string userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                var result = await _plannedBudgetService.GetPlannedBudget(userId, request);

                return Ok(result);
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpPut]
        [Authorize(AuthenticationSchemes = "Bearer")]
        public async Task<IActionResult> AddPlannedBudget([FromQuery] PlannedBudgetModel model)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState.Values.SelectMany(e => e.Errors.Select(er => er.ErrorMessage)));
            }

            try
            {
                string userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                var response = await _plannedBudgetService.AddPlannedBudget(userId, model);

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
        public async Task<IActionResult> RemoveOperation([FromRoute] int id)
        {
            try
            {
                string userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                await _plannedBudgetService.RemovePlannedBudget(userId, id);

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
        public async Task<IActionResult> UpdateOperation([FromRoute] int id, [FromQuery] PlannedBudgetModel model)
        {
            try
            {
                string userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                await _plannedBudgetService.UpdatePlannedBudget(userId, id, model);

                return Ok();
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }
    }
}
